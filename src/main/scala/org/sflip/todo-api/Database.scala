package org.sflip.todo_api

import cats._, cats.data._, cats.effect._, cats.implicits._
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.ExecutionContexts
import doobie.util.{Read, Write}
import org.sflip.todo_api.model._
import org.sflip.todo_api.model.Types._


abstract class Database {

  def getTodo(id: TodoId): Option[Todo]
  def createTodo(todo: Todo): TodoId
  def deleteTodo(id: TodoId): Unit
  def updateTodo(id: TodoId, todo: Todo): Unit
  def addTask(id: TodoId, name: String): Unit
  def deleteTask(id: TodoId, number: Int): Unit
  def updateTask(id: TodoId, number: Int, name: String): Unit

}

object Postgresql extends Database {

  object ConnectionParameters {
    val Database = "todo-api"
    val Username = "todo-api"
    val Password = "aTohdaeXeeng6eisha6aek"
  }

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    s"jdbc:postgresql:${ConnectionParameters.Database}",
    ConnectionParameters.Username,
    ConnectionParameters.Password,
  )

  // // CustomMappings allow to (de-)serialize custom types (such as Todo) directly (instead of tuples)
  // // TODO: make these work (currently fail with 'could not find implicit value for parameter ev')
  // private object CustomMappings {
  //   implicit val todoGet: Get[Todo] = Get[(String, String)].map((n, d) => Todo(n, d, List.empty))
  //   implicit val todoPut: Put[Todo] = Put[(String, String)].contramap(todo => (todo.name, todo.description))
  // }
  // import CustomMappings._

  override def getTodo(id: TodoId): Option[Todo] = getTodoIO(id).transact(xa).unsafeRunSync()
  override def createTodo(todo: Todo): TodoId = createTodoIO(todo).transact(xa).unsafeRunSync()
  override def deleteTodo(id: TodoId): Unit = deleteTodoIO(id).transact(xa).unsafeRunSync()
  override def updateTodo(id: TodoId, todo: Todo): Unit = updateTodoIO(id, todo).transact(xa).unsafeRunSync()
  override def addTask(id: TodoId, name: String): Unit = addTaskIO(id, name).transact(xa).unsafeRunSync()
  override def deleteTask(id: TodoId, number: Int): Unit = deleteTaskIO(id, number).transact(xa).unsafeRunSync()
  override def updateTask(id: TodoId, number: Int, name: String): Unit = updateTaskIO(id, number, name).transact(xa).unsafeRunSync()

  // def getTodoIO(id: TodoId): ConnectionIO[Option[Todo]] =
  // sql"""
  // SELECT todo.name, todo.description, array_agg(task.name)
  // FROM todos todo LEFT JOIN tasks task ON todo.id = task.todo_id
  // WHERE todo.id = $id
  // GROUP BY todo.id""".query[(String, String, List[String])].option map { _ map {
  // case (name, description, tasks) => Todo(name, description, tasks.map(Task(_)))
  // }}

  def getTodoIO(id: TodoId): ConnectionIO[Option[Todo]] =
    for {
      todo  <- sql" SELECT name, description FROM todos WHERE id = $id".query[(String, String)].option
      tasks <- sql"SELECT name FROM tasks WHERE todo_id = $id".query[String].to[List]
    } yield todo.map { case (name, description) => Todo(name, description, tasks.map(Task(_))) }

  def createTodoIO(todo: Todo): ConnectionIO[TodoId] =
    sql"INSERT INTO todos (name, description) VALUES (${todo.name}, ${todo.description}".update
      .withUniqueGeneratedKeys("id")

  def deleteTodoIO(id: TodoId): ConnectionIO[Unit] =
    sql"DELETE FROM todos WHERE id = $id".update.run.map(_ => ())

  def updateTodoIO(id: TodoId, todo: Todo): ConnectionIO[Unit] =
    sql"UPDATE todos SET name = ${todo.name}, description = ${todo.description} WHERE id = $id".update.run.map(_ => ())

  def addTaskIO(id: TodoId, name: String): ConnectionIO[Unit] =
    sql"INSERT INTO tasks (todo_id, name) VALUES ($id, $name)".update.run.map(_ => ())

  // TODO: Solve problem of specifying tasks with number, as SQL tables are (unordered) sets!

  def deleteTaskIO(id: TodoId, number: Int): ConnectionIO[Unit] = ???

  def updateTaskIO(id: TodoId, number: Int, name: String): ConnectionIO[Unit] = ???

}

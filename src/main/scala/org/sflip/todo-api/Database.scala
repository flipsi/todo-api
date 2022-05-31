package org.sflip.todo_api

import cats._, cats.data._, cats.effect._, cats.implicits._
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.ExecutionContexts
import doobie.util.{Read, Write}
import org.sflip.todo_api.model._
import scala.concurrent.Future

import Main.{TaskNumber, TodoId}

object Database {

  object ConnectionParameters {
    val Database = "todo-api"
    val Username = "todo-api"
    val Password = "aTohdaeXeeng6eisha6aek"
  }

  private val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    s"jdbc:postgresql:${ConnectionParameters.Database}",
    ConnectionParameters.Username,
    ConnectionParameters.Password,
  )

  // // CustomMappings allow to (de-)serialize custom types (such as Todo) directly (instead of tuples)
  // // TODO: make these work (currently fail with 'could not find implicit value for parameter ev')
  // private object CustomMappings {
  //   implicit val todoGet: Get[Todo] = Get[(String, String)].map((name, description) => Todo(name, description, List.empty))
  //   implicit val todoPut: Put[Todo] = Put[(String, String)].contramap(todo => (todo.name, todo.description))
  // }
  // import CustomMappings._

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
       todo <- sql" SELECT name, description FROM todos WHERE id = $id".query[(String, String)].option
       tasks <- sql"SELECT name FROM tasks WHERE todo_id = $id".query[String].to[List]
    } yield todo.map {case (name, description) => Todo(name, description, tasks.map(Task(_))) }

  // def getTodo(id: TodoId): Future[Option[Todo]] = futurify { t => getTodoIO(id) }
  def getTodo(id: TodoId): Future[Option[Todo]] = getTodoIO(id).transact(transactor).unsafeToFuture

}

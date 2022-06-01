package org.sflip.todo_api.api

import org.sflip.todo_api.model._
import org.sflip.todo_api.model.Types._
import org.sflip.todo_api.{Database, Postgresql}
import scala.concurrent.Future
import wvlet.airframe.launcher._

import scala.concurrent.ExecutionContext

// class CLI(db: Database) {

// Runtime error:
// 2022-06-01 13:37:38.489+0200  warn [RuntimeGenericSurface]
// Failed to instantiate TodoCommand: [java.lang.IllegalStateException] Cannot build a non-static class org.sflip.todo_api.api.CLI$TodoCommand. Call Surface.of[TodoCommand] or bind[TodoCommand].toXXX where `this` points to an instance of class org.sflip.todo_api.api.CLI
// args:[true, None, None, None, None]  - (RuntimeeGenericSurface.scala:107)
// Exception in thread "main" java.lang.IllegalArgumentException: Error occurered in launching TodoCommand: Cannot build a non-static class org.sflip.todo_api.api.CLI$TodoCommand. Call Surface.of[TodoCommand] or bind[TodoCommand].toXXX where `this` points to an instance of class org.sflip.todo_api.api.CLI

object CLI {

  lazy val db: Database = Postgresql

  def run(args: Array[String]) = Launcher.execute[TodoCommand](args)

  class TodoCommand(
      @option(prefix = "-h,--help", description = "Show help message", isHelp = true)
      displayHelp: Boolean,
      @option(prefix = "-i,--id", description = "Unique identifier of a TODO")
      id: Option[TodoId],
      @option(prefix = "-n,--name", description = "Name of a TODO or TASK")
      name: Option[String],
      @option(prefix = "-d,--description", description = "Description of a TODO")
      description: Option[String],
      @option(prefix = "-t,--task", description = "Number of a TASK of a TODO")
      taskNumber: Option[TaskNumber],
  ) {

    // TODO: exit with non-zero exit code on failures
    // TODO: more gentle error messages instead of stack traces
    // TODO: aggregate errors

    def requireId(f: TodoId => Unit): Unit = {
      id match {
        case None    => println(s"ERROR: Please specify an ID with -i or --id")
        case Some(i) => f(i)
      }
    }

    def requireName(f: String => Unit): Unit = {
      name match {
        case None    => println(s"ERROR: Please specify a name with -n or --name")
        case Some(n) => f(n)
      }
    }

    def requireTaskNumber(f: TaskNumber => Unit): Unit = {
      taskNumber match {
        case None    => println(s"ERROR: Please specify a task number with -t or --task")
        case Some(t) => f(t)
      }
    }

    def printTodo(id: TodoId, todo: Todo): Unit = {
      val todoString  = s"$id\t${todo.name.getOrElse("[no name]")}\t(${todo.description.getOrElse("no description")})"
      val tasksString = s"Tasks: ${todo.tasks.map(_.name).mkString(", ")}"
      println(s"$todoString:\t$tasksString")
    }

    @command(description = "List all TODOs")
    def list() = println("Sorry, not implemented yet.")

    @command(description = "Create a TODO")
    def create() = {
      val todo        = Todo(name, description, List.empty)
      val generatedId = db.createTodo(todo)
      println(s"Created TODO $generatedId.")
    }

    @command(description = "Show a TODO")
    def show() = requireId { id =>
      db.getTodo(id) match {
        case Some(t) => printTodo(id, t)
        case _       => println(s"No TODO with ID $id found.")
      }
    }

    @command(description = "Update a TODO")
    def update() = requireId { id =>
      db.getTodo(id) match {
        case Some(t) => db.updateTodo(id, Todo(name, description, List.empty))
        case _       => println(s"No TODO with ID $id found.")
      }
    }

    @command(description = "Delete a TODO")
    def delete() = requireId { id =>
      db.getTodo(id) match {
        case Some(t) => db.deleteTodo(id)
        case _       => println(s"No TODO with ID $id found.")
      }
    }

    @command(description = "Add a TASK to a TODO")
    def addTask() = requireId { id =>
      requireName { name =>
        db.getTodo(id) match {
          case Some(t) => db.addTask(id, name)
          case _       => println(s"No TODO with ID $id found.")
        }
      }
    }

    @command(description = "Remove a TASK from a TODO")
    def removeTask() = requireId { id =>
      requireTaskNumber { taskNumber =>
        db.getTodo(id) match {
          case Some(t) => db.deleteTask(id, taskNumber)
          case _       => println(s"No TODO with ID $id found.")
        }
      }
    }

    @command(description = "Update a TASK from a TODO")
    def updateTask() = requireId { id =>
      requireTaskNumber { taskNumber =>
        requireName { name =>
          db.getTodo(id) match {
            case Some(t) => db.updateTask(id, taskNumber, name)
            case _       => println(s"No TODO with ID $id found.")
          }
        }
      }
    }

  }

}

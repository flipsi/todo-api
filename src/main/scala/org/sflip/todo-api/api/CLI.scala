package org.sflip.todo_api.api

import org.sflip.todo_api.model._
import org.sflip.todo_api.model.Types._
import org.sflip.todo_api.Database
import scala.concurrent.Future
import wvlet.airframe.launcher._

import scala.concurrent.ExecutionContext


class CLI(db: Database) {

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

    @command(description = "List all TODOs")
    def list() = println("Sorry, not implemented yet.")

    @command(description = "Create a TODO")
    def create() = {
      println(s"Creating TODO $name.")
      val generatedId = 42
      println(s"Created TODO $generatedId.")
    }

    @command(description = "Show a TODO")
    def show() = requireId { id =>
      println(s"I will try to show TODO $id, I swear!")
    }

    @command(description = "Update a TODO")
    def update() = requireId { id =>
      println(s"I will try to update TODO $id, I swear!")
    }

    @command(description = "Delete a TODO")
    def delete() = requireId { id =>
      println(s"I will try to delete TODO $id, I swear!")
    }

    @command(description = "Add a TASK to a TODO")
    def addTask() = requireId { id =>
      requireName { name =>
        println(s"I will try add TASK $name to TODO $id, I swear!")
      }
    }

    @command(description = "Remove a TASK from a TODO")
    def removeTask() = requireId { id =>
      requireTaskNumber { taskNumber =>
        println(s"I will try remove TASK $taskNumber from TODO $id, I swear!")
      }
    }

    @command(description = "Update a TASK from a TODO")
    def updateTask() = requireId { id =>
      requireTaskNumber { taskNumber =>
        requireName { name =>
          println(s"I will try update TASK $taskNumber from TODO $id, I swear!")
        }
      }
    }

  }

}

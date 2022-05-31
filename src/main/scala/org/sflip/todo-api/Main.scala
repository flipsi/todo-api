package org.sflip.todo_api

import org.sflip.todo_api.model._
import org.sflip.todo_api.api.CLI
import scala.concurrent.Future

import scala.concurrent.ExecutionContext


object Main extends App {

  implicit val ec: ExecutionContext = ExecutionContext.global

  val db: Database = Postgresql
  val cli: CLI = new CLI(db)

  cli.run(args)

}

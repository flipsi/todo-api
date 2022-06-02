package org.sflip.todo_api

import org.sflip.todo_api.model._
import org.sflip.todo_api.api.CLI


object Main extends App {

  val db: Database = Postgresql
  // val cli: CLI = new CLI(db)
  val cli = CLI

  cli.run(args)

}

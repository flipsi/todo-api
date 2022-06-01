package org.sflip.todo_api.model

final case class Todo(name: Option[String], description: Option[String], tasks: List[Task])

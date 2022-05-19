package org.sflip.todo_api.model

final case class Todo(name: String, description: String, tasks: List[Task])

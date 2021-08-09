package me.quenchjian.data.db

import me.quenchjian.model.Task

data class TaskEntity(
  val id: String,
  val title: String,
  val description: String,
  val isComplete: Boolean
) {
  constructor(task: Task) : this(task.id, task.title, task.description, task.isCompleted)
  fun toTask() = Task(id, title, description, isComplete)
}
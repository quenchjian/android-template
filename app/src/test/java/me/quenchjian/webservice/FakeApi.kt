package me.quenchjian.webservice

import me.quenchjian.model.Task

class FakeApi : RestApi {

  private val tasks = mutableMapOf<String, Task>()

  init {
    tasks["1"] = Task("1", "Build tower in Pisa", "Ground looks good, no foundation work required.")
    tasks["2"] = Task("2", "Finish bridge in Tacoma", "Found awesome girders at half the cost!")
  }

  override suspend fun loadTask(taskId: String): Task {
    return tasks[taskId] ?: throw Exception("Task not found")
  }

  override suspend fun loadTasks(): List<Task> {
    return tasks.values.toList()
  }

  override suspend fun addTask(task: Task) {
    if (tasks[task.id] != null) {
      throw Exception("Task already added")
    }
    tasks[task.id] = task
  }

  override suspend fun editTask(task: Task) {
    val saved = loadTask(task.id)
    saved.title = task.title
    saved.description = task.description
    saved.isCompleted = task.isCompleted
  }

  override suspend fun deleteTask(taskId: String) {
    tasks.remove(taskId)
  }

  override suspend fun deleteTask(tasks: List<Task>) {
    tasks.forEach { task -> this.tasks.remove(task.id) }
  }
}

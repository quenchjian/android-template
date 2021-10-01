package me.quenchjian.webservice

import kotlinx.coroutines.delay
import me.quenchjian.model.Task
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class WebApi @Inject constructor() : RestApi {

  private val tasks = mutableMapOf<String, Task>()

  init {
    addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
    addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
  }

  override suspend fun loadTask(taskId: String): Task {
    delay()
    return tasks[taskId] ?: throw Exception("Task not found")
  }

  override suspend fun loadTasks(): List<Task> {
    delay()
    return tasks.values.toList()
  }

  override suspend fun addTask(task: Task) {
    delay()
    if (tasks[task.id] != null) {
      throw Exception("Task already added")
    }
    tasks[task.id] = task
  }

  override suspend fun editTask(task: Task) {
    delay()
    val saved = loadTask(task.id)
    saved.title = task.title
    saved.description = task.description
    saved.isCompleted = task.isCompleted
  }

  override suspend fun deleteTask(taskId: String) {
    delay()
    tasks.remove(taskId)
  }

  override suspend fun deleteTask(tasks: List<Task>) {
    delay()
    tasks.forEach { task -> this.tasks.remove(task.id) }
  }

  private suspend fun delay() {
    delay(Random.nextLong(1000, 2000))
  }

  private fun addTask(title: String, description: String) {
    val task = Task(title, description)
    tasks[task.id] = task
  }
}

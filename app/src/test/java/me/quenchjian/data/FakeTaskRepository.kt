package me.quenchjian.data

import me.quenchjian.model.Task

class FakeTaskRepository: TaskRepository {

  private val tasks = mutableMapOf<String, Task>()

  override suspend fun load(id: String): Task? {
    return tasks[id]
  }

  override suspend fun load(): List<Task> {
    return tasks.values.toList()
  }

  override suspend fun save(task: Task) {
    tasks[task.id] = task
  }

  override suspend fun saveOrUpdate(task: Task) {
    tasks[task.id] = task
  }

  override suspend fun delete(id: String) {
    tasks.remove(id)
  }

  override suspend fun delete(taskIds: List<String>) {
    taskIds.forEach { id -> tasks.remove(id) }
  }

  override suspend fun deleteCompleted() {
    val iterator = tasks.iterator()
    while (iterator.hasNext()) {
      if (iterator.next().value.isCompleted) {
        iterator.remove()
      }
    }
  }

  override suspend fun sync(tasks: List<Task>, clear: Boolean) {
    if (clear) {
      this.tasks.clear()
    }
    tasks.forEach { task -> this.tasks[task.id] = task }
  }
}

package me.quenchjian.data

import androidx.annotation.WorkerThread
import me.quenchjian.model.Task

@WorkerThread
interface TaskRepository {

  suspend fun load(id: String): Task?
  suspend fun load(): List<Task>
  suspend fun save(task: Task)
  suspend fun saveOrUpdate(task: Task)
  suspend fun delete(id: String)
  suspend fun delete(taskIds: List<String>)
  suspend fun deleteCompleted()
  suspend fun sync(tasks: List<Task>, clear: Boolean = false)
}

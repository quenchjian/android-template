package me.quenchjian.webservice

import androidx.annotation.WorkerThread
import me.quenchjian.model.Task

@WorkerThread
interface RestApi {

  suspend fun loadTask(taskId: String): Task
  suspend fun loadTasks(): List<Task>
  suspend fun addTask(task: Task)
  suspend fun editTask(task: Task)
  suspend fun deleteTask(taskId: String)
  suspend fun deleteTask(tasks: List<Task>)
}

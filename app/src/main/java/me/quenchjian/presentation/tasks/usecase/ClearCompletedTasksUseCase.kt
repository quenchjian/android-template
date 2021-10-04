package me.quenchjian.presentation.tasks.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class ClearCompletedTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<List<Task>>(scheduler) {

  @MainThread
  operator fun invoke() {
    tryInvoke { execute() }
  }

  @WorkerThread
  private suspend fun execute(): List<Task> {
    val tasks = api.loadTasks()
    val tasksToDelete = tasks.filter { it.isCompleted }
    api.deleteTask(tasksToDelete)
    repo.deleteCompleted()
    return tasks.filter { !it.isCompleted }
  }
}

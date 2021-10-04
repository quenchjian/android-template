package me.quenchjian.presentation.taskdetail.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class ChangeTaskStateUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<Task>(scheduler) {

  @MainThread
  operator fun invoke(task: Task, completed: Boolean) {
    tryInvoke { execute(task, completed) }
  }

  @WorkerThread
  private suspend fun execute(task: Task, completed: Boolean): Task {
    if (task.isCompleted == completed) return task
    val updated = task.copy(isCompleted = completed)
    api.editTask(updated)
    repo.saveOrUpdate(task)
    return updated
  }
}

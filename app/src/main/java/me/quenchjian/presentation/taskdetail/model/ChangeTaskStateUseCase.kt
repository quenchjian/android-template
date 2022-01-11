package me.quenchjian.presentation.taskdetail.model

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.UseCase
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class ChangeTaskStateUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : UseCase<ChangeTaskStateUseCase.Result>(scheduler) {

  interface Result {
    fun onLoading(active: Boolean) {}
    fun onSuccess(task: Task)
    fun onError(t: Throwable)
  }

  @MainThread
  operator fun invoke(task: Task, completed: Boolean) {
    launch {
      try {
        getListeners().forEach { it.onLoading(true) }
        val result = withContext(scheduler.io) { execute(task, completed) }
        getListeners().forEach { it.onSuccess(result) }
      } catch (t: Throwable) {
        getListeners().forEach { it.onError(t) }
      } finally {
        getListeners().forEach { it.onLoading(false) }
      }
    }
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

package me.quenchjian.presentation.taskdetail.model

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.presentation.common.model.UseCase
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : UseCase<DeleteTaskUseCase.Result>(scheduler) {

  interface Result {
    fun onLoading(active: Boolean) {}
    fun onSuccess()
    fun onError(t: Throwable)
  }

  @MainThread
  operator fun invoke(id: String) {
    launch {
      try {
        getListeners().forEach { it.onLoading(true) }
        withContext(scheduler.io) { execute(id) }
        getListeners().forEach { it.onSuccess() }
      } catch (t: Throwable) {
        getListeners().forEach { it.onError(t) }
      } finally {
        getListeners().forEach { it.onLoading(false) }
      }
    }
  }

  @WorkerThread
  private suspend fun execute(id: String) {
    api.deleteTask(id)
    repo.delete(id)
  }
}

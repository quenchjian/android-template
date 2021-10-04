package me.quenchjian.presentation.taskdetail.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<Unit>(scheduler) {

  @MainThread
  operator fun invoke(id: String) {
    tryInvoke { execute(id) }
  }

  @WorkerThread
  private suspend fun execute(id: String) {
    api.deleteTask(id)
    repo.delete(id)
  }
}

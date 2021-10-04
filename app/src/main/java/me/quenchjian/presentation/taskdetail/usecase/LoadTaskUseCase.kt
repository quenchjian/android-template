package me.quenchjian.presentation.taskdetail.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class LoadTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<Task>(scheduler) {

  private var loaded: Task? = null

  @MainThread
  operator fun invoke(id: String, reload: Boolean) {
    tryInvoke { execute(id, reload) }
  }

  @WorkerThread
  private suspend fun execute(id: String, reload: Boolean): Task {
    return if (reload) loadFromApi(id) else loaded ?: loadFromRepo(id)
  }

  private suspend fun loadFromApi(id: String): Task {
    return api.loadTask(id).also {
      repo.saveOrUpdate(it)
      loaded = it
    }
  }

  private suspend fun loadFromRepo(id: String): Task {
    return repo.load(id)?.also { loaded = it } ?: loadFromApi(id)
  }
}

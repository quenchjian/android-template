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

class LoadTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : UseCase<LoadTaskUseCase.Result>(scheduler) {

  interface Result {
    fun onLoading(active: Boolean) {}
    fun onSuccess(task: Task)
    fun onError(t: Throwable)
  }

  private var loaded: Task? = null

  @MainThread
  operator fun invoke(id: String, reload: Boolean) {
    launch {
      try {
        getListeners().forEach { it.onLoading(true) }
        val result = withContext(scheduler.io) { execute(id, reload) }
        getListeners().forEach { it.onSuccess(result) }
      } catch (t: Throwable) {
        getListeners().forEach { it.onError(t) }
      } finally {
        getListeners().forEach { it.onLoading(false) }
      }
    }
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

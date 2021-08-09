package me.quenchjian.presentation.taskdetail.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.usecase.Observable
import me.quenchjian.usecase.State
import me.quenchjian.usecase.StateObserver
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class LoadTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository
) : Observable(scheduler) {

  private var successRunner: (Task) -> Unit = {}
  private val observer = StateObserver { state ->
    @Suppress("UNCHECKED_CAST")
    when (state) {
      is State.Loading -> startRunner.invoke()
      is State.Complete -> completeRunner.invoke()
      is State.Success<*> -> successRunner.invoke(state.t as Task)
      is State.Error -> errorRunner.invoke(state.t)
    }
  }
  private var loaded: Task? = null

  fun onStart(runner: () -> Unit) = apply { startRunner = runner }
  fun onComplete(runner: () -> Unit) = apply { completeRunner = runner }
  fun onSuccess(runner: (Task) -> Unit) = apply { successRunner = runner }
  fun onError(runner: (Throwable) -> Unit) = apply { errorRunner = runner }

  @MainThread
  fun invoke(id: String, reload: Boolean) {
    subscribe(observer)
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

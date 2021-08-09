package me.quenchjian.presentation.statistics.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.presentation.statistics.Statistics
import me.quenchjian.usecase.Observable
import me.quenchjian.usecase.State
import me.quenchjian.usecase.StateObserver
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class CalculateTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository
) : Observable(scheduler) {

  private var successRunner: (Statistics) -> Unit = {}
  private val observer = StateObserver { state ->
    @Suppress("UNCHECKED_CAST")
    when (state) {
      is State.Loading -> startRunner.invoke()
      is State.Complete -> completeRunner.invoke()
      is State.Success<*> -> successRunner.invoke(state.t as Statistics)
      is State.Error -> errorRunner.invoke(state.t)
    }
  }

  fun onStart(runner: () -> Unit) = apply { startRunner = runner }
  fun onComplete(runner: () -> Unit) = apply { completeRunner = runner }
  fun onSuccess(runner: (Statistics) -> Unit) = apply { successRunner = runner }
  fun onError(runner: (Throwable) -> Unit) = apply { errorRunner = runner }

  @MainThread
  fun invoke(reload: Boolean) {
    subscribe(observer)
    tryInvoke { execute(reload) }
  }

  @WorkerThread
  private suspend fun execute(reload: Boolean): Statistics {
    val tasks = when {
      reload -> api.loadTasks().also { repo.sync(it, true) }
      else -> repo.load()
    }
    if (tasks.isEmpty()) {
      return Statistics(0f, 0f)
    }
    val activeSize = tasks.filter { !it.isCompleted }.size
    val completedSize = tasks.size - activeSize
    return Statistics(activeSize * 100f / tasks.size, completedSize * 100f / tasks.size)
  }
}

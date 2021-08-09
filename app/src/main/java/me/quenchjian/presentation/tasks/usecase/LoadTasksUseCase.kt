package me.quenchjian.presentation.tasks.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.tasks.TasksScreen
import me.quenchjian.usecase.Observable
import me.quenchjian.usecase.State
import me.quenchjian.usecase.StateObserver
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class LoadTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository
) : Observable(scheduler) {

  private var successRunner: (List<Task>) -> Unit = {}
  private val observer = StateObserver { state ->
    @Suppress("UNCHECKED_CAST")
    when (state) {
      is State.Loading -> startRunner.invoke()
      is State.Complete -> completeRunner.invoke()
      is State.Success<*> -> successRunner.invoke(state.t as List<Task>)
      is State.Error -> errorRunner.invoke(state.t)
    }
  }
  private val loaded = mutableListOf<Task>()

  fun onStart(runner: () -> Unit) = apply { startRunner = runner }
  fun onComplete(runner: () -> Unit) = apply { completeRunner = runner }
  fun onSuccess(runner: (List<Task>) -> Unit) = apply { successRunner = runner }
  fun onError(runner: (Throwable) -> Unit) = apply { errorRunner = runner }

  @MainThread
  fun invoke(reload: Boolean, filter: TasksScreen.Filter) {
    subscribe(observer)
    tryInvoke { execute(reload, filter) }
  }

  @WorkerThread
  private suspend fun execute(reload: Boolean, filter: TasksScreen.Filter): List<Task> {
    val tasks = when {
      reload -> api.loadTasks().also {
        repo.sync(it, true)
        loaded.clear()
        loaded.addAll(it)
      }
      loaded.isNotEmpty() -> loaded
      else -> repo.load()
    }
    return when (filter) {
      TasksScreen.Filter.ALL -> tasks
      TasksScreen.Filter.COMPLETED -> tasks.filter { it.isCompleted }
      TasksScreen.Filter.ACTIVE -> tasks.filter { !it.isCompleted }
    }
  }
}

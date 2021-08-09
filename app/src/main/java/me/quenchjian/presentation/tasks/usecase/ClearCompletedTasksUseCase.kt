package me.quenchjian.presentation.tasks.usecase

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

class ClearCompletedTasksUseCase @Inject constructor(
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

  fun onStart(runner: () -> Unit) = apply { startRunner = runner }
  fun onComplete(runner: () -> Unit) = apply { completeRunner = runner }
  fun onSuccess(runner: (List<Task>) -> Unit) = apply { successRunner = runner }
  fun onError(runner: (Throwable) -> Unit) = apply { errorRunner = runner }

  @MainThread
  fun invoke() {
    subscribe(observer)
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

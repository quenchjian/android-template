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

class ChangeTaskStateUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository
) : Observable(scheduler) {

  private var successRunner: () -> Unit = {}
  private val observer = StateObserver { state ->
    when (state) {
      is State.Loading -> startRunner.invoke()
      is State.Complete -> completeRunner.invoke()
      is State.Success<*> -> successRunner.invoke()
      is State.Error -> errorRunner.invoke(state.t)
    }
  }

  fun onStart(runner: () -> Unit) = apply { startRunner = runner }
  fun onComplete(runner: () -> Unit) = apply { completeRunner = runner }
  fun onSuccess(runner: () -> Unit) = apply { successRunner = runner }
  fun onError(runner: (Throwable) -> Unit) = apply { errorRunner = runner }

  @MainThread
  fun invoke(task: Task, completed: Boolean) {
    subscribe(observer)
    tryInvoke { execute(task, completed) }
  }

  @WorkerThread
  private suspend fun execute(task: Task, completed: Boolean) {
    if (task.isCompleted == completed) return
    val updated = task.copy(isCompleted = completed)
    api.editTask(updated)
    repo.saveOrUpdate(task)
  }
}

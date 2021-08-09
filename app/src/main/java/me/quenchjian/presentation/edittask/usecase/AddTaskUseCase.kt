package me.quenchjian.presentation.edittask.usecase

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

class AddTaskUseCase @Inject constructor(
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
  fun invoke(title: String, description: String) {
    subscribe(observer)
    tryInvoke { execute(title, description) }
  }

  @WorkerThread
  private suspend fun execute(title: String, description: String) {
    when {
      title.isEmpty() -> throw TitleEmptyError
      description.isEmpty() -> throw DescriptionEmptyError
      else -> {
        val task = Task(title = title, description = description)
        api.addTask(task)
        repo.save(task)
      }
    }
  }
}

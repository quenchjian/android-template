package me.quenchjian.presentation.edittask.model

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

class EditTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : UseCase<EditTaskUseCase.Result>(scheduler) {

  interface Result {
    fun onLoading(active: Boolean) {}
    fun onSuccess(task: Task)
    fun onError(t: Throwable)
  }

  @MainThread
  operator fun invoke(task: Task) {
    launch {
      try {
        getListeners().forEach { it.onLoading(true) }
        val result = withContext(scheduler.io) { execute(task) }
        getListeners().forEach { it.onSuccess(result) }
      } catch (t: Throwable) {
        getListeners().forEach { it.onError(t) }
      } finally {
        getListeners().forEach { it.onLoading(false) }
      }
    }
  }

  @WorkerThread
  private suspend fun execute(task: Task): Task {
    when {
      task.title.isEmpty() -> throw TitleEmptyError
      task.description.isEmpty() -> throw DescriptionEmptyError
      else -> {
        api.editTask(task)
        repo.saveOrUpdate(task)
        return task
      }
    }
  }
}

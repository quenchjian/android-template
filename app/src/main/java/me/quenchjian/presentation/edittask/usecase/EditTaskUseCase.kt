package me.quenchjian.presentation.edittask.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class EditTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<Task>(scheduler) {

  @MainThread
  operator fun invoke(task: Task) {
    tryInvoke { execute(task) }
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

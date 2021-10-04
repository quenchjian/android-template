package me.quenchjian.presentation.edittask.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<Task>(scheduler) {

  @MainThread
  operator fun invoke(title: String, description: String) {
    tryInvoke { execute(title, description) }
  }

  @WorkerThread
  private suspend fun execute(title: String, description: String): Task {
    when {
      title.isEmpty() -> throw TitleEmptyError
      description.isEmpty() -> throw DescriptionEmptyError
      else -> {
        val task = Task(title = title, description = description)
        api.addTask(task)
        repo.save(task)
        return task
      }
    }
  }
}

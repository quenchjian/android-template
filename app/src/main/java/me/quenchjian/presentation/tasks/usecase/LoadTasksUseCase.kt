package me.quenchjian.presentation.tasks.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.presentation.tasks.TasksScreen
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class LoadTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<List<Task>>(scheduler) {

  private val loaded = mutableListOf<Task>()

  @MainThread
  operator fun invoke(reload: Boolean, filter: TasksScreen.Filter) {
    tryInvoke { execute(reload, filter) }
  }

  @WorkerThread
  private suspend fun execute(reload: Boolean, filter: TasksScreen.Filter): List<Task> {
    val tasks = when {
      reload -> loadFromApi()
      loaded.isNotEmpty() -> loaded
      else -> loadFromRepo()
    }
    return when (filter) {
      TasksScreen.Filter.ALL -> tasks
      TasksScreen.Filter.COMPLETED -> tasks.filter { it.isCompleted }
      TasksScreen.Filter.ACTIVE -> tasks.filter { !it.isCompleted }
    }
  }

  private suspend fun loadFromApi(): List<Task> {
    return api.loadTasks().also {
      repo.sync(it, true)
      loaded.clear()
      loaded.addAll(it)
    }
  }

  private suspend fun loadFromRepo(): List<Task> {
    val tasks = repo.load().also {
      loaded.clear()
      loaded.addAll(it)
    }
    return when {
      tasks.isNotEmpty() -> tasks
      else -> loadFromApi()
    }
  }
}

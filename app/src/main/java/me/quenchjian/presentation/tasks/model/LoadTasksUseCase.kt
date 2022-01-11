package me.quenchjian.presentation.tasks.model

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

class LoadTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : UseCase<LoadTasksUseCase.Result>(scheduler) {

  interface Result {
    fun onLoading(active: Boolean) {}
    fun onSuccess(tasks: List<Task>)
    fun onError(t: Throwable)
  }

  private val loaded = mutableListOf<Task>()

  @MainThread
  operator fun invoke(reload: Boolean, filter: Filter) {
    launch {
      try {
        getListeners().forEach { it.onLoading(true) }
        val result = withContext(scheduler.io) { execute(reload, filter) }
        getListeners().forEach { it.onSuccess(result) }
      } catch (t: Throwable) {
        getListeners().forEach { it.onError(t) }
      } finally {
        getListeners().forEach { it.onLoading(false) }
      }
    }
  }

  @WorkerThread
  private suspend fun execute(reload: Boolean, filter: Filter): List<Task> {
    val tasks = when {
      reload -> loadFromApi()
      loaded.isNotEmpty() -> loaded
      else -> loadFromRepo()
    }
    return when (filter) {
      Filter.ALL -> tasks
      Filter.COMPLETED -> tasks.filter { it.isCompleted }
      Filter.ACTIVE -> tasks.filter { !it.isCompleted }
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

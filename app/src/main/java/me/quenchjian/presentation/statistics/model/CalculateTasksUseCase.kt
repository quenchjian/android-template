package me.quenchjian.presentation.statistics.model

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

class CalculateTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : UseCase<CalculateTasksUseCase.Result>(scheduler) {

  interface Result {
    fun onLoading(active: Boolean) {}
    fun onSuccess(statistics: Statistics)
    fun onError(t: Throwable)
  }

  @MainThread
  operator fun invoke(reload: Boolean) {
    launch {
      try {
        getListeners().forEach { it.onLoading(true) }
        val result = withContext(scheduler.io) { execute(reload) }
        getListeners().forEach { it.onSuccess(result) }
      } catch (t: Throwable) {
        getListeners().forEach { it.onError(t) }
      } finally {
        getListeners().forEach { it.onLoading(false) }
      }
    }
  }

  @WorkerThread
  private suspend fun execute(reload: Boolean): Statistics {
    val tasks = when {
      reload -> loadFromApi()
      else -> loadFromRepo()
    }
    if (tasks.isEmpty()) {
      return Statistics(0f, 0f)
    }
    val activeSize = tasks.filter { !it.isCompleted }.size
    val completedSize = tasks.size - activeSize
    return Statistics(activeSize * 100f / tasks.size, completedSize * 100f / tasks.size)
  }

  private suspend fun loadFromApi(): List<Task> {
    return api.loadTasks().also { repo.sync(it, true) }
  }

  private suspend fun loadFromRepo(): List<Task> {
    val saved = repo.load()
    return if (saved.isEmpty()) loadFromApi() else saved
  }
}

package me.quenchjian.presentation.statistics.usecase

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import me.quenchjian.concurrent.Schedulers
import me.quenchjian.data.TaskRepository
import me.quenchjian.presentation.common.model.Observable
import me.quenchjian.presentation.statistics.Statistics
import me.quenchjian.webservice.RestApi
import javax.inject.Inject

class CalculateTasksUseCase @Inject constructor(
  scheduler: Schedulers,
  private val api: RestApi,
  private val repo: TaskRepository,
) : Observable<Statistics>(scheduler) {

  @MainThread
  operator fun invoke(reload: Boolean) {
    tryInvoke { execute(reload) }
  }

  @WorkerThread
  private suspend fun execute(reload: Boolean): Statistics {
    val tasks = when {
      reload -> api.loadTasks().also { repo.sync(it, true) }
      else -> repo.load()
    }
    if (tasks.isEmpty()) {
      return Statistics(0f, 0f)
    }
    val activeSize = tasks.filter { !it.isCompleted }.size
    val completedSize = tasks.size - activeSize
    return Statistics(activeSize * 100f / tasks.size, completedSize * 100f / tasks.size)
  }
}

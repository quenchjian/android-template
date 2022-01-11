package me.quenchjian.presentation.statistics.controller

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.presentation.common.Screen
import me.quenchjian.presentation.statistics.model.CalculateTasksUseCase
import me.quenchjian.presentation.statistics.model.Statistics
import me.quenchjian.presentation.statistics.view.StatisticsView
import javax.inject.Inject

@HiltViewModel
class StatisticsController @Inject constructor(
  private val calculateTasks: CalculateTasksUseCase,
) : ViewModel(), Screen.Controller<StatisticsView> {

  override var view: StatisticsView? = null

  init {
    calculateTasks.registerListener(object : CalculateTasksUseCase.Result {
      override fun onLoading(active: Boolean) {
        view?.toggleCalculating(active)
      }

      override fun onSuccess(statistics: Statistics) {
        view?.showStatistics(statistics)
      }

      override fun onError(t: Throwable) {
        handleError(t)
      }
    })
  }

  fun calculate(reload: Boolean) {
    calculateTasks(reload)
  }

  override fun onCleared() {
    calculateTasks.dispose()
  }
}

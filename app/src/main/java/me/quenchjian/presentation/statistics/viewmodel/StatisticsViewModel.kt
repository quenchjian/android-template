package me.quenchjian.presentation.statistics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.presentation.common.viewmodel.BaseViewModel
import me.quenchjian.presentation.statistics.model.CalculateTasksUseCase
import me.quenchjian.presentation.statistics.model.Statistics
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
  private val calculateTasks: CalculateTasksUseCase,
) : BaseViewModel() {

  val loading: LiveData<Boolean> = MutableLiveData()
  val statistics: LiveData<Statistics> = MutableLiveData()
  val error: LiveData<String> = MutableLiveData()

  init {
    calculateTasks.registerListener(object : CalculateTasksUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess(statistics: Statistics) {
        this@StatisticsViewModel.statistics.mutable().value = statistics
      }

      override fun onError(t: Throwable) {
        error.mutable().value = t.message
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

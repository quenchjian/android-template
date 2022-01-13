package me.quenchjian.presentation.tasks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.viewmodel.BaseViewModel
import me.quenchjian.presentation.taskdetail.model.ChangeTaskStateUseCase
import me.quenchjian.presentation.tasks.model.ClearCompletedTasksUseCase
import me.quenchjian.presentation.tasks.model.Filter
import me.quenchjian.presentation.tasks.model.LoadTasksUseCase
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
  private val loadTasks: LoadTasksUseCase,
  private val changeTaskState: ChangeTaskStateUseCase,
  private val clearTasks: ClearCompletedTasksUseCase,
) : BaseViewModel() {

  val filter: LiveData<Filter?> = MutableLiveData()
  val loading: LiveData<Boolean> = MutableLiveData(false)
  val tasks: LiveData<List<Task>> = MutableLiveData()
  val error: LiveData<String> = MutableLiveData()
  val completeState: LiveData<CompleteState> = MutableLiveData()

  init {
    loadTasks.registerListener(object : LoadTasksUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess(tasks: List<Task>) {
        this@TasksViewModel.tasks.mutable().value = tasks
      }

      override fun onError(t: Throwable) {
        error.mutable().value = t.message
      }
    })
    changeTaskState.registerListener(object : ChangeTaskStateUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess(task: Task) {
        completeState.mutable().value = CompleteState.Success
      }

      override fun onError(t: Throwable) {
        error.mutable().value = t.message
        completeState.mutable().value = CompleteState.Failure
      }
    })
    clearTasks.registerListener(object : ClearCompletedTasksUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess(activeTasks: List<Task>) {
        tasks.mutable().value = activeTasks
      }

      override fun onError(t: Throwable) {
        error.mutable().value = t.message
      }
    })
  }

  fun loadTasks(reload: Boolean, filter: Filter = Filter.ALL) {
    if (loading.value == true) return
    this.filter.mutable().value = filter
    loadTasks.invoke(reload, filter)
  }

  fun toggleTaskState(task: Task, completed: Boolean) {
    if (loading.value == true) return
    changeTaskState.invoke(task, completed)
  }

  fun clearCompletedTask() {
    if (loading.value == true) return
    clearTasks()
  }

  override fun onCleared() {
    loadTasks.dispose()
    changeTaskState.dispose()
    clearTasks.dispose()
  }

  sealed interface CompleteState {
    object Success : CompleteState
    object Failure : CompleteState
  }
}

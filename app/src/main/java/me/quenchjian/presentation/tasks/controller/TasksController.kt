package me.quenchjian.presentation.tasks.controller

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.controller.Controller
import me.quenchjian.presentation.taskdetail.model.ChangeTaskStateUseCase
import me.quenchjian.presentation.tasks.model.Filter
import me.quenchjian.presentation.tasks.model.ClearCompletedTasksUseCase
import me.quenchjian.presentation.tasks.model.LoadTasksUseCase
import me.quenchjian.presentation.tasks.view.TasksView
import javax.inject.Inject

@HiltViewModel
class TasksController @Inject constructor(
  private val loadTasks: LoadTasksUseCase,
  private val changeTaskState: ChangeTaskStateUseCase,
  private val clearTasks: ClearCompletedTasksUseCase,
) : ViewModel(), Controller<TasksView> {

  override var view: TasksView? = null

  private var filter = Filter.ALL
  private var loading: Boolean = false
  private lateinit var taskToChange: Task

  init {
    loadTasks.registerListener(object : LoadTasksUseCase.Result {
      override fun onLoading(active: Boolean) {
        toggleLoading(active)
      }

      override fun onSuccess(tasks: List<Task>) {
        view?.showTasks(tasks, filter)
      }

      override fun onError(t: Throwable) {
        handleError(t)
      }
    })
    changeTaskState.registerListener(object : ChangeTaskStateUseCase.Result {
      override fun onLoading(active: Boolean) {
        toggleLoading(active)
      }

      override fun onSuccess(task: Task) {}
      override fun onError(t: Throwable) {
        handleError(t)
        view?.showChangeTaskStateFail(taskToChange)
      }
    })
    clearTasks.registerListener(object : ClearCompletedTasksUseCase.Result {
      override fun onLoading(active: Boolean) {
        toggleLoading(active)
      }

      override fun onSuccess(activeTasks: List<Task>) {
        view?.showTasks(activeTasks, Filter.ALL)
      }

      override fun onError(t: Throwable) {
        handleError(t)
      }
    })
  }

  fun loadTasks(reload: Boolean, filter: Filter) {
    if (loading) return
    this.filter = filter
    loadTasks.invoke(reload, filter)
  }

  fun toggleTaskState(task: Task, completed: Boolean) {
    if (loading) return
    taskToChange = task
    changeTaskState.invoke(task, completed)
  }

  fun clearCompletedTask() {
    if (loading) return
    clearTasks()
  }

  private fun toggleLoading(loading: Boolean) {
    this.loading = loading
    view?.toggleLoading(loading)
  }

  override fun onCleared() {
    loadTasks.dispose()
    changeTaskState.dispose()
    clearTasks.dispose()
  }
}

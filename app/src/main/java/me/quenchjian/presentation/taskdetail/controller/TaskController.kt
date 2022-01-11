package me.quenchjian.presentation.taskdetail.controller

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.Screen
import me.quenchjian.presentation.taskdetail.model.ChangeTaskStateUseCase
import me.quenchjian.presentation.taskdetail.model.DeleteTaskUseCase
import me.quenchjian.presentation.taskdetail.model.LoadTaskUseCase
import me.quenchjian.presentation.taskdetail.view.TaskView
import javax.inject.Inject

@HiltViewModel
class TaskController @Inject constructor(
  private val loadTask: LoadTaskUseCase,
  private val deleteTask: DeleteTaskUseCase,
  private val changeTaskState: ChangeTaskStateUseCase,
) : ViewModel(), Screen.Controller<TaskView> {

  override var view: TaskView? = null
  private var task: Task? = null
  private var loading = false

  init {
    loadTask.registerListener(object : LoadTaskUseCase.Result {
      override fun onLoading(active: Boolean) {
        toggleLoading(active)
      }

      override fun onSuccess(task: Task) {
        this@TaskController.task = task
        view?.showTask(task)
      }

      override fun onError(t: Throwable) {
        handleError(t)
      }
    })
    deleteTask.registerListener(object : DeleteTaskUseCase.Result {
      override fun onLoading(active: Boolean) {
        toggleLoading(active)
      }

      override fun onSuccess() {
        view?.showTaskDeleted()
        task = null
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
        view?.showChangeTaskStateFail(task!!)
      }
    })
  }

  fun loadTask(id: String, reload: Boolean) {
    if (loading) return
    loadTask.invoke(id, reload)
  }

  fun deleteTask(id: String) {
    if (task == null) return // already deleted
    if (loading) return
    deleteTask.invoke(id)
  }

  fun toggleTaskState(isCompleted: Boolean) {
    val task = task ?: return // nothing to do
    if (loading) return
    changeTaskState(task, isCompleted)
  }

  private fun toggleLoading(loading: Boolean) {
    this.loading = loading
    view?.toggleLoading(loading)
  }

  override fun onCleared() {
    loadTask.dispose()
    deleteTask.dispose()
    changeTaskState.dispose()
  }
}

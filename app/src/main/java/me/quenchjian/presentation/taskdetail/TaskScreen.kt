package me.quenchjian.presentation.taskdetail

import me.quenchjian.model.Task
import me.quenchjian.presentation.common.Screen

interface TaskScreen {

  interface View : Screen.View {
    fun toggleLoading(loading: Boolean)
    fun showTask(task: Task)
    fun showTaskDeleted()
    fun showChangeTaskStateFail(task: Task)

    fun onBackClick(click: () -> Unit)
    fun onDeleteClick(click: () -> Unit)
    fun onEditClick(click: () -> Unit)
    fun onCheckBoxClick(click: (isChecked: Boolean) -> Unit)
    fun onSwipeRefresh(swipe: () -> Unit)
  }

  interface Controller : Screen.Controller<View> {
    fun loadTask(id: String, reload: Boolean)
    fun deleteTask(id: String)
    fun toggleTaskState(task: Task, isCompleted: Boolean)
  }
}

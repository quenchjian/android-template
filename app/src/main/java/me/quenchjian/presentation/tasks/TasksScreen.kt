package me.quenchjian.presentation.tasks

import me.quenchjian.model.Task
import me.quenchjian.presentation.common.Screen
import me.quenchjian.presentation.drawer.DrawerScreen

interface TasksScreen {

  interface View : DrawerScreen {
    fun toggleLoading(loading: Boolean)
    fun showFilterMenu(click: (filter: Filter) -> Unit)
    fun showTasks(tasks: List<Task>, filter: Filter)
    fun showChangeTaskStateFail(task: Task)

    fun onMenuClick(click: (menu: Menu) -> Unit)
    fun onSwipeRefresh(swipe: () -> Unit)
    fun onTaskClick(click: (task: Task) -> Unit)
    fun onTaskCompleteClick(click: (checked: Boolean, task: Task) -> Unit)
    fun onAddClick(click: () -> Unit)
  }

  interface Controller : Screen.Controller<View> {
    fun loadTasks(reload: Boolean, filter: Filter)
    fun toggleTaskState(task: Task, completed: Boolean)
    fun clearCompletedTask()
  }

  enum class Menu {
    FILTER, CLEAR, REFRESH
  }
  enum class Filter {
    ALL, COMPLETED, ACTIVE
  }
}

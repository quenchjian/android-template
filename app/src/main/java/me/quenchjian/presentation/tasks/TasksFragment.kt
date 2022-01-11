package me.quenchjian.presentation.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTasksBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.drawer.DrawerFragment
import me.quenchjian.presentation.drawer.Menu
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.TaskFragment
import me.quenchjian.presentation.tasks.controller.TasksController
import me.quenchjian.presentation.tasks.model.Filter
import me.quenchjian.presentation.tasks.view.ContextMenu
import me.quenchjian.presentation.tasks.view.TasksView

@AndroidEntryPoint
class TasksFragment : DrawerFragment<TasksView>(R.layout.view_tasks) {

  private val view by createView { v -> TasksView(ViewTasksBinding.bind(v)) }
  private val controller: TasksController by viewModels()

  override val drawerView get() = view
  override fun getCurrentMenu() = Menu.TASKS

  private var filter = Filter.ALL

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let { this.filter = it.filter }
  }

  override fun onStart() {
    super.onStart()
    controller.view = view
    view.onMenuClick { menu ->
      when (menu) {
        ContextMenu.FILTER -> view.showFilterMenu {
          val filter = when (it.itemId) {
            R.id.filter_active -> Filter.ACTIVE
            R.id.filter_completed -> Filter.COMPLETED
            else -> Filter.ALL
          }
          controller.loadTasks(false, filter)
        }
        ContextMenu.CLEAR -> controller.clearCompletedTask()
        ContextMenu.REFRESH -> controller.loadTasks(true, filter)
      }
    }
    view.onSwipeRefresh { controller.loadTasks(true, filter) }
    view.onTaskClick { task -> navigator.goTo(TaskFragment.Key(task.id)) }
    view.onTaskCompleteClick { checked, task -> controller.toggleTaskState(task, checked) }
    view.onAddClick { navigator.goTo(EditTaskFragment.Key()) }
    controller.loadTasks(true, filter)
  }

  override fun onStop() {
    super.onStop()
    controller.view = null
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.filter = filter
  }

  companion object {
    private var Bundle.filter: Filter
      get() = Filter.valueOf(getString("key-tasks-filter", Filter.ALL.name))
      set(value) = putString("key-tasks-filter", value.name)
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment(): Fragment = TasksFragment()
  }
}

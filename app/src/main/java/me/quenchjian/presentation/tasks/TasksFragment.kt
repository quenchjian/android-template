package me.quenchjian.presentation.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTasksBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.drawer.DrawerFragment
import me.quenchjian.presentation.drawer.Menu
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.TaskFragment
import me.quenchjian.presentation.tasks.model.Filter
import me.quenchjian.presentation.tasks.view.ContextMenu
import me.quenchjian.presentation.tasks.view.TasksView
import me.quenchjian.presentation.tasks.viewmodel.TasksViewModel

@AndroidEntryPoint
class TasksFragment : DrawerFragment<TasksViewModel, TasksView>(R.layout.view_tasks) {

  override val v by createView { v -> TasksView(ViewTasksBinding.bind(v)) }
  override val vm: TasksViewModel by viewModels()

  override val drawerView get() = v
  override fun getCurrentMenu() = Menu.TASKS

  override fun bindViewProperty() {
    super.bindViewProperty()
    if (vm.filter.value == null) {
      vm.loadTasks(true, Filter.ALL)
    }
  }

  override fun bindViewCommand() {
    super.bindViewCommand()
    v.onMenuClick { menu ->
      when (menu) {
        ContextMenu.FILTER -> Unit
        ContextMenu.CLEAR -> vm.clearCompletedTask()
        ContextMenu.REFRESH -> vm.loadTasks(true)
      }
    }
    v.onFilterMenuClick { item ->
      val filter = when (item.itemId) {
        R.id.filter_active -> Filter.ACTIVE
        R.id.filter_completed -> Filter.COMPLETED
        else -> Filter.ALL
      }
      vm.loadTasks(false, filter)
    }
    v.onSwipeRefresh { vm.loadTasks(true) }
    v.onTaskClick { task -> navigator.goTo(TaskFragment.Key(task.id)) }
    v.onTaskCompleteClick { checked, task -> vm.toggleTaskState(task, checked) }
    v.onAddClick { navigator.goTo(EditTaskFragment.Key()) }
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment(): Fragment = TasksFragment()
  }
}

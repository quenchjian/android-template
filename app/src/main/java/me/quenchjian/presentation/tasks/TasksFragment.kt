package me.quenchjian.presentation.tasks

import android.os.Bundle
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTasksBinding
import me.quenchjian.model.Task
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.createView
import me.quenchjian.presentation.drawer.DrawerFragment
import me.quenchjian.presentation.drawer.DrawerScreen
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.TaskFragment
import me.quenchjian.presentation.taskdetail.usecase.ChangeTaskStateUseCase
import me.quenchjian.presentation.tasks.usecase.ClearCompletedTasksUseCase
import me.quenchjian.presentation.tasks.usecase.LoadTasksUseCase
import javax.inject.Inject

@AndroidEntryPoint
class TasksFragment : DrawerFragment<TasksScreen.View>(R.layout.view_tasks),
  TasksScreen.Controller {

  @Inject lateinit var loadTasks: LoadTasksUseCase
  @Inject lateinit var changeTaskState: ChangeTaskStateUseCase
  @Inject lateinit var clearTasks: ClearCompletedTasksUseCase

  override val view by createView { v -> TasksView(ViewTasksBinding.bind(v)) }
  override fun getCurrentMenu() = DrawerScreen.Menu.TASKS

  private var filter = TasksScreen.Filter.ALL
  private var loading: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    filter = savedInstanceState.getFilter()
  }

  override fun onStart() {
    super.onStart()
    view.onMenuClick { menu ->
      when (menu) {
        TasksScreen.Menu.FILTER -> view.showFilterMenu { filter -> loadTasks(false, filter) }
        TasksScreen.Menu.CLEAR -> clearCompletedTask()
        TasksScreen.Menu.REFRESH -> loadTasks(true, filter)
      }
    }
    view.onSwipeRefresh { loadTasks(true, filter) }
    view.onTaskClick { task -> navProvider.get(this).goTo(TaskFragment.Key(task.id)) }
    view.onTaskCompleteClick { checked, task -> toggleTaskState(task, checked) }
    view.onAddClick { navProvider.get(this).goTo(EditTaskFragment.Key()) }
    loadTasks(true, filter)
  }

  override fun onStop() {
    super.onStop()
    loadTasks.dispose()
    changeTaskState.dispose()
    clearTasks.dispose()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.saveFilter(filter)
  }

  override fun loadTasks(reload: Boolean, filter: TasksScreen.Filter) {
    if (loading) return
    this.filter = filter
    loadTasks.onStart { view.toggleLoading(true.also { loading = it }) }
      .onComplete { view.toggleLoading(false.also { loading = it }) }
      .onSuccess { view.showTasks(it, filter) }
      .onError { handleError(it) }
      .invoke(reload, filter)
  }

  override fun toggleTaskState(task: Task, completed: Boolean) {
    changeTaskState.onStart { view.toggleLoading(true.also { loading = it }) }
      .onComplete { view.toggleLoading(false.also { loading = it }) }
      .onError {
        handleError(it)
        view.showChangeTaskStateFail(task)
      }
      .invoke(task, completed)
  }

  override fun clearCompletedTask() {
    clearTasks.onStart { view.toggleLoading(true.also { loading = it }) }
      .onComplete { view.toggleLoading(false.also { loading = it }) }
      .onSuccess { view.showTasks(it, TasksScreen.Filter.ALL) }
      .onError { handleError(it) }
      .invoke()
  }

  companion object {
    private fun Bundle?.getFilter() =
      TasksScreen.Filter.values()[this?.getInt("key-tasks-filter") ?: 0]

    private fun Bundle.saveFilter(filter: TasksScreen.Filter) =
      putInt("key-tasks-filter", filter.ordinal)
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment() = TasksFragment()
  }
}

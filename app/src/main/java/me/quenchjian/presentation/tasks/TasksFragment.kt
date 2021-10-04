package me.quenchjian.presentation.tasks

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTasksBinding
import me.quenchjian.model.Task
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.common.view.createView
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
  override val drawer by lazy { view }
  override fun getCurrentMenu() = DrawerScreen.Menu.TASKS

  private var filter = TasksScreen.Filter.ALL
  private var loading: Boolean = false
  private lateinit var taskToChange: Task

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let { this.filter = it.filter }
  }

  override fun onStart() {
    super.onStart()
    loadTasks.subscribe(State.Observer(
      onStart = { toggleLoading(true) },
      onComplete = { toggleLoading(false) },
      onSuccess = { drawer.showTasks(it, filter) },
      onError = this::handleError
    ))
    changeTaskState.subscribe(State.Observer(
      onStart = { toggleLoading(true) },
      onComplete = { toggleLoading(false) },
      onError = {
        handleError(it)
        drawer.showChangeTaskStateFail(taskToChange)
      }
    ))
    clearTasks.subscribe(State.Observer(
      onStart = { toggleLoading(true) },
      onComplete = { toggleLoading(false) },
      onSuccess = { drawer.showTasks(it, TasksScreen.Filter.ALL) },
      onError = this::handleError
    ))
    drawer.onMenuClick { menu ->
      when (menu) {
        TasksScreen.Menu.FILTER -> drawer.showFilterMenu { filter -> loadTasks(false, filter) }
        TasksScreen.Menu.CLEAR -> clearCompletedTask()
        TasksScreen.Menu.REFRESH -> loadTasks(true, filter)
      }
    }
    drawer.onSwipeRefresh { loadTasks(true, filter) }
    drawer.onTaskClick { task -> navigator.goTo(TaskFragment.Key(task.id)) }
    drawer.onTaskCompleteClick { checked, task -> toggleTaskState(task, checked) }
    drawer.onAddClick { navigator.goTo(EditTaskFragment.Key()) }
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
    outState.filter = filter
  }

  override fun loadTasks(reload: Boolean, filter: TasksScreen.Filter) {
    if (loading) return
    this.filter = filter
    loadTasks.invoke(reload, filter)
  }

  override fun toggleTaskState(task: Task, completed: Boolean) {
    if (loading) return
    taskToChange = task
    changeTaskState.invoke(task, completed)
  }

  override fun clearCompletedTask() {
    if (loading) return
    clearTasks()
  }

  private fun toggleLoading(loading: Boolean) {
    this.loading = loading
    drawer.toggleLoading(loading)
  }

  companion object {
    private var Bundle.filter: TasksScreen.Filter
      get() = TasksScreen.Filter.valueOf(getString("key-tasks-filter", TasksScreen.Filter.ALL.name))
      set(value) = putString("key-tasks-filter", value.name)
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment() = TasksFragment()
  }
}

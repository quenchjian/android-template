package me.quenchjian.presentation.taskdetail

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.usecase.ChangeTaskStateUseCase
import me.quenchjian.presentation.taskdetail.usecase.DeleteTaskUseCase
import me.quenchjian.presentation.taskdetail.usecase.LoadTaskUseCase
import javax.inject.Inject

@AndroidEntryPoint
class TaskFragment : KeyedFragment(R.layout.view_task), TaskScreen.Controller {

  @Inject lateinit var loadTask: LoadTaskUseCase
  @Inject lateinit var deleteTask: DeleteTaskUseCase
  @Inject lateinit var changeTaskState: ChangeTaskStateUseCase

  override val view by createView { TaskView(ViewTaskBinding.bind(it)) }

  private lateinit var taskId: String
  private var task: Task? = null
  private var loading = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    taskId = getKey<Key>().taskId
  }

  override fun onStart() {
    super.onStart()
    loadTask.subscribe(State.Observer(
      onStart = { toggleLoading(true) },
      onComplete = { toggleLoading(false) },
      onSuccess = { view.showTask(it.also { this.task = it }) },
      onError = this::handleError
    ))
    deleteTask.subscribe(State.Observer(
      onStart = { toggleLoading(true) },
      onComplete = { toggleLoading(false) },
      onSuccess = {
        view.showTaskDeleted()
        task = null
      },
      onError = this::handleError
    ))
    changeTaskState.subscribe(State.Observer(
      onStart = { toggleLoading(true) },
      onComplete = { toggleLoading(false) },
      onError = {
        handleError(it)
        view.showChangeTaskStateFail(task!!)
      }
    ))
    view.onBackClick { navigator.goBack() }
    view.onDeleteClick { deleteTask(taskId) }
    view.onCheckBoxClick { toggleTaskState(task!!, it) }
    view.onEditClick { navigator.goTo(EditTaskFragment.Key(taskId)) }
    view.onSwipeRefresh { loadTask(taskId, true) }
    loadTask(taskId, false)
  }

  override fun onStop() {
    super.onStop()
    loadTask.dispose()
    deleteTask.dispose()
    changeTaskState.dispose()
  }

  override fun loadTask(id: String, reload: Boolean) {
    if (loading) return
    loadTask.invoke(id, reload)
  }

  override fun deleteTask(id: String) {
    if (task == null) return // already deleted
    if (loading) return
    deleteTask.invoke(id)
  }

  override fun toggleTaskState(task: Task, isCompleted: Boolean) {
    if (loading) return
    changeTaskState(task, isCompleted)
  }

  private fun toggleLoading(loading: Boolean) {
    this.loading = loading
    view.toggleLoading(loading)
  }

  @Parcelize
  class Key(val taskId: String) : FragmentKey() {
    override fun instantiateFragment(): Fragment {
      return TaskFragment().apply { arguments = bundleOf("arg-task-id" to taskId) }
    }
  }
}

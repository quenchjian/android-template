package me.quenchjian.presentation.taskdetail

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dagger.hilt.EntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.Navigator
import me.quenchjian.presentation.common.createView
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.usecase.ChangeTaskStateUseCase
import me.quenchjian.presentation.taskdetail.usecase.DeleteTaskUseCase
import me.quenchjian.presentation.taskdetail.usecase.LoadTaskUseCase
import javax.inject.Inject

@EntryPoint
class TaskFragment : KeyedFragment(R.layout.view_task), TaskScreen.Controller {

  @Inject lateinit var navProvider: Navigator.Provider
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
    view.onBackClick { navProvider.get(this).goBack() }
    view.onDeleteClick { deleteTask(taskId) }
    view.onCheckBoxClick { toggleTaskState(task!!, it) }
    view.onEditClick { navProvider.get(this).goTo(EditTaskFragment.Key(taskId)) }
    view.onSwipeRefresh { loadTask(taskId, true) }
    loadTask(taskId, false)
  }

  override fun loadTask(id: String, reload: Boolean) {
    if (loading) return
    loadTask.onStart { view.toggleLoading(true.also { loading = it }) }
      .onComplete { view.toggleLoading(false.also { loading = it }) }
      .onSuccess { view.showTask(it.also { task = it }) }
      .onError { handleError(it) }
      .invoke(id, reload)
  }

  override fun deleteTask(id: String) {
    if (task == null) return // already deleted
    if (loading) return
    deleteTask.onStart { view.toggleLoading(true.also { loading = it }) }
      .onComplete { view.toggleLoading(false.also { loading = it }) }
      .onSuccess {
        view.showTaskDeleted()
        task = null
      }
      .onError { handleError(it) }
      .invoke(id)
  }

  override fun toggleTaskState(task: Task, isCompleted: Boolean) {
    if (loading) return
    changeTaskState.onStart { view.toggleLoading(true.also { loading = it }) }
      .onComplete { view.toggleLoading(false.also { loading = it }) }
      .onSuccess {}
      .onError {
        handleError(it)
        view.showChangeTaskStateFail(task)
      }
      .invoke(task, isCompleted)
  }

  @Parcelize
  class Key(val taskId: String) : FragmentKey() {
    override fun instantiateFragment(): Fragment {
      return TaskFragment().apply { arguments = bundleOf("arg-task-id" to taskId) }
    }
  }
}

package me.quenchjian.presentation.edittask

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.edittask.usecase.AddTaskUseCase
import me.quenchjian.presentation.edittask.usecase.DescriptionEmptyError
import me.quenchjian.presentation.edittask.usecase.EditTaskUseCase
import me.quenchjian.presentation.edittask.usecase.TitleEmptyError
import me.quenchjian.presentation.taskdetail.usecase.LoadTaskUseCase
import javax.inject.Inject

@AndroidEntryPoint
class EditTaskFragment : KeyedFragment(R.layout.view_edit_task), EditTaskScreen.Controller {

  @Inject lateinit var loadTask: LoadTaskUseCase
  @Inject lateinit var addTask: AddTaskUseCase
  @Inject lateinit var editTask: EditTaskUseCase

  override val view by createView { EditTaskView(ViewEditTaskBinding.bind(it)) }

  private var taskId: String? = null
  private lateinit var task: Task

  private val addEditObserver = State.Observer<Task>(
    onSuccess = { navigator.goBack() },
    onError = this::handleError
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    taskId = getKey<Key>().taskId
  }

  override fun onStart() {
    super.onStart()
    loadTask.subscribe(State.Observer(
      onSuccess = { view.showTask(it.also { task = it }) },
      onError = this::handleError
    ))
    addTask.subscribe(addEditObserver)
    editTask.subscribe(addEditObserver)
    view.onSaveClick { saveTask(it, taskId == null) }
    if (taskId != null) {
      loadTask(taskId!!)
    }
  }

  override fun onStop() {
    super.onStop()
    loadTask.dispose()
    addTask.dispose()
    editTask.dispose()
  }

  override fun loadTask(id: String) {
    loadTask(id, true)
  }

  override fun saveTask(input: EditTaskScreen.Input, add: Boolean) {
    if (add) {
      addTask(input.title, input.description)
    } else {
      editTask(task.copy(title = input.title, description = input.description))
    }
  }

  override fun handleError(t: Throwable) {
    when (t) {
      is TitleEmptyError -> view.showError(getString(R.string.empty_task_title))
      is DescriptionEmptyError -> view.showError(getString(R.string.empty_task_description))
      else -> super.handleError(t)
    }
  }

  @Parcelize
  class Key(val taskId: String? = null) : FragmentKey() {
    override fun instantiateFragment(): Fragment = EditTaskFragment()
  }
}

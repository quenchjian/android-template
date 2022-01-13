package me.quenchjian.presentation.edittask.controller

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.controller.Controller
import me.quenchjian.presentation.common.model.InputError
import me.quenchjian.presentation.edittask.view.TaskInput
import me.quenchjian.presentation.edittask.model.AddTaskUseCase
import me.quenchjian.presentation.edittask.model.EditTaskUseCase
import me.quenchjian.presentation.edittask.view.EditTaskView
import me.quenchjian.presentation.taskdetail.model.LoadTaskUseCase
import javax.inject.Inject

@HiltViewModel
class EditTaskController @Inject constructor(
  private val loadTask: LoadTaskUseCase,
  private val addTask: AddTaskUseCase,
  private val editTask: EditTaskUseCase,
) : ViewModel(), Controller<EditTaskView> {

  override var view: EditTaskView? = null
  private lateinit var task: Task
  private val saveTaskResult = SaveTaskResult()

  init {
    loadTask.registerListener(LoadTaskResult())
    addTask.registerListener(saveTaskResult)
    editTask.registerListener(saveTaskResult)
  }

  fun loadTask(id: String) {
    loadTask.invoke(id, true)
  }

  fun saveTask(input: TaskInput, add: Boolean) {
    if (add) {
      addTask(input.title, input.description)
    } else {
      editTask(task.copy(title = input.title, description = input.description))
    }
  }

  fun onTaskSaved(runner: () -> Unit) {
    saveTaskResult.taskSaved = runner
  }

  override fun handleError(t: Throwable) {
    when (t) {
      is InputError -> view?.showInputError(t)
      else -> super.handleError(t)
    }
  }

  override fun onCleared() {
    loadTask.dispose()
    addTask.dispose()
    editTask.dispose()
  }

  private inner class LoadTaskResult: LoadTaskUseCase.Result {
    override fun onSuccess(task: Task) {
      this@EditTaskController.task = task
      view?.showTask(task)
    }

    override fun onError(t: Throwable) {
      handleError(t)
    }
  }

  private inner class SaveTaskResult: AddTaskUseCase.Result, EditTaskUseCase.Result {
    var taskSaved: () -> Unit = {}

    override fun onLoading(active: Boolean) {}

    override fun onSuccess(task: Task) {
      taskSaved.invoke()
    }

    override fun onError(t: Throwable) {
      handleError(t)
    }
  }
}

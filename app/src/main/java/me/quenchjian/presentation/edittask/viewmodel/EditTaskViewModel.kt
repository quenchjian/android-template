package me.quenchjian.presentation.edittask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.InputError
import me.quenchjian.presentation.common.viewmodel.BaseViewModel
import me.quenchjian.presentation.edittask.model.AddTaskUseCase
import me.quenchjian.presentation.edittask.model.EditTaskUseCase
import me.quenchjian.presentation.edittask.view.TaskInput
import me.quenchjian.presentation.taskdetail.model.LoadTaskUseCase
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
  private val loadTask: LoadTaskUseCase,
  private val addTask: AddTaskUseCase,
  private val editTask: EditTaskUseCase,
) : BaseViewModel() {

  private var taskCache: Task? = null
  private val saveTaskResult = SaveTaskResult()

  val task: LiveData<Task> = MutableLiveData()
  val inputError: LiveData<InputError> = MutableLiveData()
  val error: LiveData<String> = MutableLiveData()

  init {
    loadTask.registerListener(LoadTaskResult())
    addTask.registerListener(saveTaskResult)
    editTask.registerListener(saveTaskResult)
  }

  fun loadTask(id: String) {
    loadTask.invoke(id, true)
  }

  fun saveTask(input: TaskInput) {
    val task = taskCache
    if (task == null) {
      addTask(input.title, input.description)
    } else {
      editTask(task.copy(title = input.title, description = input.description))
    }
  }

  fun onTaskSaved(runner: () -> Unit) {
    saveTaskResult.taskSaved = runner
  }

  private fun handleError(t: Throwable) {
    when (t) {
      is InputError -> inputError.mutable().value = t
      else -> error.mutable().value = t.message
    }
  }

  override fun onCleared() {
    loadTask.dispose()
    addTask.dispose()
    editTask.dispose()
  }

  private inner class LoadTaskResult : LoadTaskUseCase.Result {
    override fun onSuccess(task: Task) {
      taskCache = task
      this@EditTaskViewModel.task.mutable().value = task
    }

    override fun onError(t: Throwable) {
      handleError(t)
    }
  }

  private inner class SaveTaskResult : AddTaskUseCase.Result, EditTaskUseCase.Result {
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

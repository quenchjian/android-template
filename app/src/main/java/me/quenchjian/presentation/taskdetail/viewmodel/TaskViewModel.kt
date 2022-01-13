package me.quenchjian.presentation.taskdetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.viewmodel.BaseViewModel
import me.quenchjian.presentation.taskdetail.model.ChangeTaskStateUseCase
import me.quenchjian.presentation.taskdetail.model.DeleteTaskUseCase
import me.quenchjian.presentation.taskdetail.model.LoadTaskUseCase
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
  private val loadTask: LoadTaskUseCase,
  private val deleteTask: DeleteTaskUseCase,
  private val changeTaskState: ChangeTaskStateUseCase,
) : BaseViewModel() {

  private var taskCache: Task? = null

  val loading: LiveData<Boolean> = MutableLiveData(false)
  val task: LiveData<Task> = MutableLiveData()
  val error: LiveData<String> = MutableLiveData()
  val deleteState: LiveData<DeleteTaskState> = MutableLiveData()
  val completeState: LiveData<CompleteTaskState> = MutableLiveData()

  init {
    loadTask.registerListener(object : LoadTaskUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess(task: Task) {
        taskCache = task
        this@TaskViewModel.task.mutable().value = task
      }

      override fun onError(t: Throwable) {
        error.mutable().value = t.message
      }
    })
    deleteTask.registerListener(object : DeleteTaskUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess() {
        deleteState.mutable().value = DeleteTaskState.Success
        taskCache = null
      }

      override fun onError(t: Throwable) {
        deleteState.mutable().value = DeleteTaskState.Failure
        error.mutable().value = t.message
      }
    })
    changeTaskState.registerListener(object : ChangeTaskStateUseCase.Result {
      override fun onLoading(active: Boolean) {
        loading.mutable().value = active
      }

      override fun onSuccess(task: Task) {
        completeState.mutable().value = CompleteTaskState.Success
      }

      override fun onError(t: Throwable) {
        completeState.mutable().value = CompleteTaskState.Failure
        error.mutable().value = t.message
      }
    })
  }

  fun loadTask(id: String, reload: Boolean) {
    if (loading.value == true) return
    loadTask.invoke(id, reload)
  }

  fun deleteTask(id: String) {
    if (taskCache == null) return // already deleted
    if (loading.value == true) return
    deleteTask.invoke(id)
  }

  fun toggleTaskState(isCompleted: Boolean) {
    val task = taskCache ?: return // nothing to do
    if (loading.value == true) return
    changeTaskState(task, isCompleted)
  }

  override fun onCleared() {
    loadTask.dispose()
    deleteTask.dispose()
    changeTaskState.dispose()
  }

  sealed interface DeleteTaskState {
    object Success : DeleteTaskState
    object Failure : DeleteTaskState
  }

  sealed interface CompleteTaskState {
    object Success : CompleteTaskState
    object Failure : CompleteTaskState
  }
}

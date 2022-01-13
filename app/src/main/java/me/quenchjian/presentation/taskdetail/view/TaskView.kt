package me.quenchjian.presentation.taskdetail.view

import android.view.View
import android.widget.CheckBox
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.view.MvvmView
import me.quenchjian.presentation.taskdetail.viewmodel.TaskViewModel

class TaskView(private val binding: ViewTaskBinding) : MvvmView<TaskViewModel> {

  override val root = binding.root

  private lateinit var task: Task

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    binding.toolbar.setNavigationContentDescription(R.string.toolbar_navigation_content_description)
    binding.toolbar.inflateMenu(R.menu.task_action)
  }

  override fun initViewModel(vm: TaskViewModel) {
    vm.loading.observe(lifecycleOwner) { toggleLoading(it) }
    vm.task.observe(lifecycleOwner) { showTask(it) }
    vm.deleteState.observe(lifecycleOwner) {
      if (it == TaskViewModel.DeleteTaskState.Success) {
        showTaskDeleted()
      }
    }
    vm.completeState.observe(lifecycleOwner) {
      if (it == TaskViewModel.CompleteTaskState.Failure) {
        binding.checkboxTaskState.isChecked = task.isCompleted
      }
    }
  }

  private fun toggleLoading(loading: Boolean) {
    binding.swiperefreshTask.isRefreshing = loading
  }

  private fun showTask(task: Task) {
    this.task = task
    binding.checkboxTaskState.isChecked = task.isCompleted
    binding.textTaskTitle.text = task.title
    binding.textTaskDescription.text = task.description
  }

  private fun showTaskDeleted() {
    binding.checkboxTaskState.visibility = View.GONE
    binding.textTaskTitle.text = string(R.string.no_data)
    binding.textTaskDescription.visibility = View.GONE
  }

  fun onBackClick(click: () -> Unit) {
    binding.toolbar.setNavigationOnClickListener { click() }
  }

  fun onDeleteClick(click: () -> Unit) {
    binding.toolbar.setOnMenuItemClickListener {
      click()
      true
    }
  }

  fun onEditClick(click: () -> Unit) {
    binding.fabEdit.setOnClickListener { click() }
  }

  fun onCheckBoxClick(click: (isChecked: Boolean) -> Unit) {
    binding.checkboxTaskState.setOnClickListener { v -> click((v as CheckBox).isChecked) }
  }

  fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshTask.setOnRefreshListener(swipe)
  }
}

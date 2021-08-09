package me.quenchjian.presentation.taskdetail

import android.view.View
import android.widget.CheckBox
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.model.Task

class TaskView(private val binding: ViewTaskBinding) : TaskScreen.View {

  override val root = binding.root

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    binding.toolbar.inflateMenu(R.menu.task_action)
  }

  override fun toggleLoading(loading: Boolean) {
    binding.swiperefreshTask.isRefreshing = loading
  }

  override fun showTask(task: Task) {
    binding.checkboxTaskState.isChecked = task.isCompleted
    binding.textTaskTitle.text = task.title
    binding.textTaskDescription.text = task.description
  }

  override fun showTaskDeleted() {
    binding.checkboxTaskState.visibility = View.GONE
    binding.textTaskTitle.text = string(R.string.no_data)
    binding.textTaskDescription.visibility = View.GONE
  }

  override fun showChangeTaskStateFail(task: Task) {
    binding.checkboxTaskState.isChecked = task.isCompleted
  }

  override fun onBackClick(click: () -> Unit) {
    binding.toolbar.setNavigationOnClickListener { click() }
  }

  override fun onDeleteClick(click: () -> Unit) {
    binding.toolbar.setOnMenuItemClickListener {
      click()
      true
    }
  }

  override fun onEditClick(click: () -> Unit) {
    binding.fabEdit.setOnClickListener { click() }
  }

  override fun onCheckBoxClick(click: (isChecked: Boolean) -> Unit) {
    binding.checkboxTaskState.setOnClickListener { v -> click((v as CheckBox).isChecked) }
  }

  override fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshTask.setOnRefreshListener(swipe)
  }
}

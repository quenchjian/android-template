package me.quenchjian.presentation.taskdetail.view

import android.view.View
import android.widget.CheckBox
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.view.MvcView

class TaskView(private val binding: ViewTaskBinding) : MvcView {

  override val root = binding.root

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    binding.toolbar.setNavigationContentDescription(R.string.toolbar_navigation_content_description)
    binding.toolbar.inflateMenu(R.menu.task_action)
  }

  fun toggleLoading(loading: Boolean) {
    binding.swiperefreshTask.isRefreshing = loading
  }

  fun showTask(task: Task) {
    binding.checkboxTaskState.isChecked = task.isCompleted
    binding.textTaskTitle.text = task.title
    binding.textTaskDescription.text = task.description
  }

  fun showTaskDeleted() {
    binding.checkboxTaskState.visibility = View.GONE
    binding.textTaskTitle.text = string(R.string.no_data)
    binding.textTaskDescription.visibility = View.GONE
  }

  fun showChangeTaskStateFail(task: Task) {
    binding.checkboxTaskState.isChecked = task.isCompleted
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

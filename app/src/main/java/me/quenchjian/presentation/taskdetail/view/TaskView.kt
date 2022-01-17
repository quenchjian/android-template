package me.quenchjian.presentation.taskdetail.view

import android.view.View
import android.widget.CheckBox
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.view.MvvmView
import me.quenchjian.presentation.taskdetail.viewmodel.TaskViewModel

class TaskView(private val binding: ViewTaskBinding) : MvvmView<TaskViewModel>() {

  override val root = binding.root

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    binding.toolbar.setNavigationContentDescription(R.string.toolbar_navigation_content_description)
    binding.toolbar.inflateMenu(R.menu.task_action)
  }

  var loading: Boolean = false
    set(value) {
      field = value
      binding.swiperefreshTask.isRefreshing = value
    }
  var task: Task? = null
    set(value) {
      field = value
      completed = value?.isCompleted
      title = value?.title
      description = value?.description
    }
  var completed: Boolean? = false
    set(value) {
      field = value
      binding.checkboxTaskState.visibility = if (value == null) View.GONE else View.VISIBLE
      binding.checkboxTaskState.isChecked = value ?: false
    }
  private var title: String? = ""
    set(value) {
      field = value
      binding.textTaskTitle.text = if (value.isNullOrEmpty()) string(R.string.no_data) else value
    }
  private var description: String? = ""
    set(value) {
      field = value
      binding.textTaskDescription.visibility = if (value == null) View.GONE else View.VISIBLE
      binding.textTaskDescription.text = value
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

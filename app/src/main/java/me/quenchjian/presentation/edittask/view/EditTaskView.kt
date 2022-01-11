package me.quenchjian.presentation.edittask.view

import me.quenchjian.R
import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.Screen
import me.quenchjian.presentation.common.model.InputError
import me.quenchjian.presentation.edittask.model.DescriptionEmptyError
import me.quenchjian.presentation.edittask.model.TitleEmptyError

class EditTaskView(private val binding: ViewEditTaskBinding) : Screen.View {

  override val root = binding.root

  fun showTask(task: Task) {
    binding.editTextTitle.setText(task.title)
    binding.editTextDesc.setText(task.description)
  }

  fun showInputError(error: InputError) {
    when(error) {
      TitleEmptyError -> showError(string(R.string.empty_task_title))
      DescriptionEmptyError -> showError(string(R.string.empty_task_description))
    }
  }

  fun onSaveClick(click: (TaskInput) -> Unit) {
    binding.fabSave.setOnClickListener {
      val title = binding.editTextTitle.text.toString()
      val desc = binding.editTextDesc.text.toString()
      click(TaskInput(title, desc))
    }
  }
}

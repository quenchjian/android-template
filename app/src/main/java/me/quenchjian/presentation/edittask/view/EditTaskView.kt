package me.quenchjian.presentation.edittask.view

import me.quenchjian.R
import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.InputError
import me.quenchjian.presentation.common.view.MvvmView
import me.quenchjian.presentation.edittask.model.DescriptionEmptyError
import me.quenchjian.presentation.edittask.model.TitleEmptyError
import me.quenchjian.presentation.edittask.viewmodel.EditTaskViewModel

class EditTaskView(private val binding: ViewEditTaskBinding) : MvvmView<EditTaskViewModel>() {

  override val root = binding.root

  var task: Task? = null
    set(value) {
      field = value
      value ?: return
      binding.editTextTitle.setText(value.title)
      binding.editTextDesc.setText(value.description)
    }
  var inputError: InputError? = null
    set(value) {
      field = value
      error = when (value) {
        TitleEmptyError -> string(R.string.empty_task_title)
        DescriptionEmptyError -> string(R.string.empty_task_description)
        else -> ""
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

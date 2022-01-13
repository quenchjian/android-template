package me.quenchjian.presentation.edittask.view

import me.quenchjian.R
import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.InputError
import me.quenchjian.presentation.common.view.MvvmView
import me.quenchjian.presentation.edittask.model.DescriptionEmptyError
import me.quenchjian.presentation.edittask.model.TitleEmptyError
import me.quenchjian.presentation.edittask.viewmodel.EditTaskViewModel

class EditTaskView(private val binding: ViewEditTaskBinding) : MvvmView<EditTaskViewModel> {

  override val root = binding.root

  override fun initViewModel(vm: EditTaskViewModel) {
    vm.task.observe(lifecycleOwner) { showTask(it) }
    vm.inputError.observe(lifecycleOwner) { showInputError(it) }
    vm.error.observe(lifecycleOwner) { showError(it) }
  }

  private fun showTask(task: Task) {
    binding.editTextTitle.setText(task.title)
    binding.editTextDesc.setText(task.description)
  }

  private fun showInputError(error: InputError) {
    when (error) {
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

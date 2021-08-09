package me.quenchjian.presentation.edittask

import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.model.Task

class EditTaskView(private val binding: ViewEditTaskBinding) : EditTaskScreen.View {

  override val root = binding.root

  override fun showTask(task: Task) {
    binding.editTextTitle.setText(task.title)
    binding.editTextDesc.setText(task.description)
  }

  override fun onSaveClick(click: (EditTaskScreen.Input) -> Unit) {
    binding.fabSave.setOnClickListener {
      val title = binding.editTextTitle.text.toString()
      val desc = binding.editTextDesc.text.toString()
      click(EditTaskScreen.Input(title, desc))
    }
  }
}

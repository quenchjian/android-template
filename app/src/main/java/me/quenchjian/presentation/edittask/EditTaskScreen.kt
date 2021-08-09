package me.quenchjian.presentation.edittask

import me.quenchjian.model.Task
import me.quenchjian.presentation.common.Screen

interface EditTaskScreen {

  data class Input(val title: String, val description: String)

  interface View : Screen.View {

    fun showTask(task: Task)
    fun onSaveClick(click: (Input) -> Unit)
  }

  interface Controller : Screen.Controller<View> {

    fun loadTask(id: String)
    fun saveTask(input: Input, add: Boolean)
  }
}

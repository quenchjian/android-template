package me.quenchjian.presentation.edittask

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.edittask.controller.EditTaskController
import me.quenchjian.presentation.edittask.view.EditTaskView

@AndroidEntryPoint
class EditTaskFragment : KeyedFragment(R.layout.view_edit_task) {

  private val view: EditTaskView by createView { EditTaskView(ViewEditTaskBinding.bind(it)) }
  private val controller: EditTaskController by viewModels()

  private var taskId: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    taskId = getKey<Key>().taskId
  }

  override fun onStart() {
    super.onStart()
    controller.view = view
    controller.onTaskSaved { navigator.goBack() }
    view.onSaveClick { controller.saveTask(it, taskId == null) }
    if (taskId != null) {
      controller.loadTask(taskId!!)
    }
  }

  override fun onStop() {
    super.onStop()
    controller.view = null
  }

  @Parcelize
  class Key(val taskId: String? = null) : FragmentKey() {
    override fun instantiateFragment(): Fragment = EditTaskFragment()
  }
}

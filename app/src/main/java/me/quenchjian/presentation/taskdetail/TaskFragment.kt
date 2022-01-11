package me.quenchjian.presentation.taskdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.controller.TaskController
import me.quenchjian.presentation.taskdetail.view.TaskView

@AndroidEntryPoint
class TaskFragment : KeyedFragment(R.layout.view_task) {

  private val view by createView { TaskView(ViewTaskBinding.bind(it)) }
  private val controller: TaskController by viewModels()

  private lateinit var taskId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    taskId = getKey<Key>().taskId
  }

  override fun onStart() {
    super.onStart()
    controller.view = view
    view.onBackClick { navigator.goBack() }
    view.onDeleteClick { controller.deleteTask(taskId) }
    view.onCheckBoxClick { controller.toggleTaskState(it) }
    view.onEditClick { navigator.goTo(EditTaskFragment.Key(taskId)) }
    view.onSwipeRefresh { controller.loadTask(taskId, true) }
    controller.loadTask(taskId, false)
  }

  override fun onStop() {
    super.onStop()
    controller.view = null
  }

  @Parcelize
  class Key(val taskId: String) : FragmentKey() {
    override fun instantiateFragment(): Fragment = TaskFragment()
  }
}

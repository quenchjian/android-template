package me.quenchjian.presentation.taskdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewTaskBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.BaseFragment
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.edittask.EditTaskFragment
import me.quenchjian.presentation.taskdetail.view.TaskView
import me.quenchjian.presentation.taskdetail.viewmodel.TaskViewModel

@AndroidEntryPoint
class TaskFragment : BaseFragment<TaskViewModel, TaskView>(R.layout.view_task) {

  override val v by createView { TaskView(ViewTaskBinding.bind(it)) }
  override val vm: TaskViewModel by viewModels()

  private lateinit var taskId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    taskId = getKey<Key>().taskId
  }

  override fun bindViewProperty() {
    super.bindViewProperty()
    vm.loadTask(taskId, false)
  }

  override fun bindViewCommand() {
    super.bindViewCommand()
    v.onBackClick { navigator.goBack() }
    v.onDeleteClick { vm.deleteTask(taskId) }
    v.onCheckBoxClick { vm.toggleTaskState(it) }
    v.onEditClick { navigator.goTo(EditTaskFragment.Key(taskId)) }
    v.onSwipeRefresh { vm.loadTask(taskId, true) }
  }

  @Parcelize
  class Key(val taskId: String) : FragmentKey() {
    override fun instantiateFragment(): Fragment = TaskFragment()
  }
}

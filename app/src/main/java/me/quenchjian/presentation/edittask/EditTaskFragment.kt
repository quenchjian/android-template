package me.quenchjian.presentation.edittask

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewEditTaskBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.BaseFragment
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.edittask.view.EditTaskView
import me.quenchjian.presentation.edittask.viewmodel.EditTaskViewModel

@AndroidEntryPoint
class EditTaskFragment : BaseFragment<EditTaskViewModel, EditTaskView>(R.layout.view_edit_task) {

  override val v: EditTaskView by createView { EditTaskView(ViewEditTaskBinding.bind(it)) }
  override val vm: EditTaskViewModel by viewModels()

  private var taskId: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    taskId = getKey<Key>().taskId
  }

  override fun bindViewProperty() {
    super.bindViewProperty()
    if (taskId != null) {
      vm.loadTask(taskId!!)
    }
  }

  override fun bindViewCommand() {
    vm.onTaskSaved { navigator.goBack() }
    v.onSaveClick { vm.saveTask(it) }
  }

  @Parcelize
  class Key(val taskId: String? = null) : FragmentKey() {
    override fun instantiateFragment(): Fragment = EditTaskFragment()
  }
}

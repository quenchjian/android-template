package me.quenchjian.presentation.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.quenchjian.R
import me.quenchjian.databinding.ItemTaskBinding
import me.quenchjian.databinding.ViewTasksBinding
import me.quenchjian.model.Task
import me.quenchjian.presentation.drawer.DrawerView

class TasksView(
  private val binding: ViewTasksBinding
) : DrawerView(binding.navigation), TasksScreen.View {

  override val root: View = binding.root

  init {
    binding.toolbar.inflateMenu(R.menu.tasks_action)
    binding.recyclerTasks.adapter = Adapter()
  }

  override fun toggleLoading(loading: Boolean) {
    binding.swiperefreshTasks.isRefreshing = loading
  }

  override fun showFilterMenu(click: (filter: TasksScreen.Filter) -> Unit) {
    val view = binding.toolbar.findViewById<View>(R.id.action_filter) ?: return
    val popup = PopupMenu(context, view).apply { menuInflater.inflate(R.menu.tasks_filter, menu) }
    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.filter_active -> click(TasksScreen.Filter.ACTIVE)
        R.id.filter_completed -> click(TasksScreen.Filter.COMPLETED)
        else -> click(TasksScreen.Filter.ALL)
      }
      true
    }
    popup.show()
  }

  override fun showTasks(tasks: List<Task>, filter: TasksScreen.Filter) {
    binding.toolbar.title = when (filter) {
      TasksScreen.Filter.ALL -> string(R.string.label_all)
      TasksScreen.Filter.COMPLETED -> string(R.string.label_completed)
      TasksScreen.Filter.ACTIVE -> string(R.string.label_active)
    }
    (binding.recyclerTasks.adapter as Adapter).submitList(tasks)
  }

  override fun showChangeTaskStateFail(task: Task) {
    (binding.recyclerTasks.adapter as Adapter).submit(task)
  }

  override fun onMenuClick(click: (menu: TasksScreen.Menu) -> Unit) {
    binding.toolbar.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.action_filter -> click(TasksScreen.Menu.FILTER)
        R.id.action_clear -> click(TasksScreen.Menu.CLEAR)
        R.id.action_refresh -> click(TasksScreen.Menu.REFRESH)
      }
      true
    }
  }

  override fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshTasks.setOnRefreshListener { swipe() }
  }

  override fun onTaskClick(click: (task: Task) -> Unit) {
    (binding.recyclerTasks.adapter as Adapter).itemClick = click
  }

  override fun onTaskCompleteClick(click: (checked: Boolean, task: Task) -> Unit) {
    (binding.recyclerTasks.adapter as Adapter).itemCheck = click
  }

  override fun onAddClick(click: () -> Unit) {
    binding.fabAdd.setOnClickListener { click() }
  }

  private class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemTaskBinding.bind(view)

    fun bind(task: Task) {
      binding.checkboxTaskState.isChecked = task.isCompleted
      binding.textTaskText.text = if (task.title.isNotEmpty()) task.title else task.description
    }
  }

  private class TaskDiff : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
      return oldItem == newItem
    }
  }

  private class Adapter : ListAdapter<Task, Holder>(TaskDiff()) {

    var itemClick: (Task) -> Unit = {}
    var itemCheck: (Boolean, Task) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
      val inflater = LayoutInflater.from(parent.context)
      val holder = Holder(inflater.inflate(R.layout.item_task, parent, false))
      holder.binding.apply {
        root.setOnClickListener { itemClick(getItem(holder.adapterPosition)) }
        checkboxTaskState.setOnClickListener {
          val task = getItem(holder.adapterPosition)
          val checked = checkboxTaskState.isChecked
          if (checked != task.isCompleted) {
            itemCheck(checked, task)
          }
        }
      }
      return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
      holder.bind(getItem(position))
    }

    fun submit(task: Task) {
      var index = -1
      for ((i, t) in currentList.withIndex()) {
        if (t.id == task.id) {
          index = i
          break
        }
      }
      if (index >= 0) {
        notifyItemChanged(index)
      }
    }
  }
}

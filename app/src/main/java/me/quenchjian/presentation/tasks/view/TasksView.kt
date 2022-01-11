package me.quenchjian.presentation.tasks.view

import android.view.LayoutInflater
import android.view.MenuItem
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
import me.quenchjian.presentation.tasks.model.Filter

class TasksView(private val binding: ViewTasksBinding) : DrawerView(binding.navigation) {

  override val root: View = binding.root

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
    binding.toolbar.setNavigationContentDescription(R.string.toolbar_navigation_content_description)
    binding.toolbar.title = string(R.string.label_all)
    binding.toolbar.inflateMenu(R.menu.tasks_action)
    binding.recyclerTasks.adapter = Adapter()
  }

  fun toggleLoading(loading: Boolean) {
    binding.swiperefreshTasks.isRefreshing = loading
    (binding.recyclerTasks.adapter as Adapter).loading = loading
  }

  fun showFilterMenu(click: (item: MenuItem) -> Unit) {
    val view = binding.toolbar.findViewById<View>(R.id.action_filter) ?: return
    val popup = PopupMenu(context, view)
    popup.inflate(R.menu.tasks_filter)
    popup.setOnMenuItemClickListener {
      click(it)
      true
    }
    popup.show()
  }

  fun showTasks(tasks: List<Task>, filter: Filter) {
    binding.toolbar.title = when (filter) {
      Filter.ALL -> string(R.string.label_all)
      Filter.COMPLETED -> string(R.string.label_completed)
      Filter.ACTIVE -> string(R.string.label_active)
    }
    (binding.recyclerTasks.adapter as Adapter).submitList(tasks)
  }

  fun showChangeTaskStateFail(task: Task) {
    (binding.recyclerTasks.adapter as Adapter).submit(task)
  }

  override fun onNavigationIconClick(click: () -> Unit) {
    binding.toolbar.setNavigationOnClickListener { click() }
  }

  fun onMenuClick(click: (menu: ContextMenu) -> Unit) {
    binding.toolbar.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.action_filter -> click(ContextMenu.FILTER)
        R.id.action_clear -> click(ContextMenu.CLEAR)
        R.id.action_refresh -> click(ContextMenu.REFRESH)
      }
      true
    }
  }

  fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshTasks.setOnRefreshListener { swipe() }
  }

  fun onTaskClick(click: (task: Task) -> Unit) {
    (binding.recyclerTasks.adapter as Adapter).itemClick = click
  }

  fun onTaskCompleteClick(click: (checked: Boolean, task: Task) -> Unit) {
    (binding.recyclerTasks.adapter as Adapter).itemCheck = click
  }

  fun onAddClick(click: () -> Unit) {
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

    var loading: Boolean = false
    var itemClick: (Task) -> Unit = {}
    var itemCheck: (Boolean, Task) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
      val inflater = LayoutInflater.from(parent.context)
      val holder = Holder(inflater.inflate(R.layout.item_task, parent, false))
      holder.binding.apply {
        root.setOnClickListener {
          if (loading) return@setOnClickListener
          itemClick(getItem(holder.adapterPosition))
        }
        checkboxTaskState.setOnClickListener {
          val task = getItem(holder.adapterPosition)
          if (loading) {
            checkboxTaskState.isChecked = task.isCompleted
            return@setOnClickListener
          }
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

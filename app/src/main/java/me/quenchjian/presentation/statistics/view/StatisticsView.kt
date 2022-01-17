package me.quenchjian.presentation.statistics.view

import android.view.View
import me.quenchjian.R
import me.quenchjian.databinding.ViewStaticticsBinding
import me.quenchjian.presentation.drawer.DrawerView
import me.quenchjian.presentation.statistics.model.Statistics
import me.quenchjian.presentation.statistics.viewmodel.StatisticsViewModel

class StatisticsView(private val binding: ViewStaticticsBinding) : DrawerView<StatisticsViewModel>(binding.navigation) {

  override val root: View = binding.root

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
    binding.toolbar.setNavigationContentDescription(R.string.toolbar_navigation_content_description)
    binding.toolbar.title = string(R.string.statistics_title)
  }

  var calculating: Boolean = false
    set(value) {
      field = value
      binding.swiperefreshStatistics.isRefreshing = value
    }
  var statistics: Statistics? = null
    set(value) {
      field = value
      value ?: return
      binding.textStatisticsEmpty.visibility = if (value.isEmpty) View.VISIBLE else View.GONE
      binding.textStatisticsActive.visibility = if (value.isEmpty) View.GONE else View.VISIBLE
      binding.textStatisticsCompleted.visibility = if (value.isEmpty) View.GONE else View.VISIBLE

      binding.textStatisticsActive.text = string(R.string.statistics_active_tasks, value.active)
      binding.textStatisticsCompleted.text = string(R.string.statistics_completed_tasks, value.completed)
    }

  override fun onNavigationIconClick(click: () -> Unit) {
    binding.toolbar.setNavigationOnClickListener { click() }
  }

  fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshStatistics.setOnRefreshListener { swipe() }
  }
}

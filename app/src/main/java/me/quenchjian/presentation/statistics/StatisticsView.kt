package me.quenchjian.presentation.statistics

import android.view.View
import me.quenchjian.R
import me.quenchjian.databinding.ViewStaticticsBinding
import me.quenchjian.presentation.drawer.DrawerView

class StatisticsView(
  private val binding: ViewStaticticsBinding,
) : DrawerView(binding.navigation), StatisticsScreen.View {

  override val root: View = binding.root

  init {
    binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
    binding.toolbar.title = string(R.string.statistics_title)
  }

  override fun toggleCalculating(active: Boolean) {
    binding.swiperefreshStatistics.isRefreshing = active
  }

  override fun showStatistics(statistics: Statistics) {
    binding.textStatisticsEmpty.visibility = if (statistics.isEmpty) View.VISIBLE else View.GONE
    binding.textStatisticsActive.visibility = if (statistics.isEmpty) View.GONE else View.VISIBLE
    binding.textStatisticsCompleted.visibility = if (statistics.isEmpty) View.GONE else View.VISIBLE

    binding.textStatisticsActive.text = string(R.string.statistics_active_tasks, statistics.active)
    binding.textStatisticsCompleted.text = string(R.string.statistics_completed_tasks, statistics.completed)
  }

  override fun onNavigationIconClick(click: () -> Unit) {
    binding.toolbar.setNavigationOnClickListener { click() }
  }

  override fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshStatistics.setOnRefreshListener { swipe() }
  }
}

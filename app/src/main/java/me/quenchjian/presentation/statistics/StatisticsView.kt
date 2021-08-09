package me.quenchjian.presentation.statistics

import android.view.View
import me.quenchjian.databinding.ViewStaticticsBinding
import me.quenchjian.presentation.drawer.DrawerView

class StatisticsView(
  private val binding: ViewStaticticsBinding
) : DrawerView(binding.navigation), StatisticsScreen.View {

  override val root: View = binding.root

  override fun toggleCalculating(active: Boolean) {
    binding.swiperefreshStatistics.isRefreshing = active
  }

  override fun showStatistics(statistics: Statistics) {
    binding.textStatisticsEmpty.visibility = if (statistics.isEmpty) View.GONE else View.VISIBLE
    binding.textStatisticsActive.visibility = if (statistics.isEmpty) View.VISIBLE else View.GONE
    binding.textStatisticsCompleted.visibility = if (statistics.isEmpty) View.VISIBLE else View.GONE

    binding.textStatisticsActive.text = "%d%%".format(statistics.activePercent)
    binding.textStatisticsCompleted.text = "%d%%".format(statistics.completedPercent)
  }

  override fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshStatistics.setOnRefreshListener { swipe() }
  }
}

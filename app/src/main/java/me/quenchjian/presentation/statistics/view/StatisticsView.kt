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

  override fun initViewModel(vm: StatisticsViewModel) {
    vm.loading.observe(lifecycleOwner) { toggleCalculating(it) }
    vm.statistics.observe(lifecycleOwner) { showStatistics(it) }
    vm.error.observe(lifecycleOwner) { showError(it) }
  }

  private fun toggleCalculating(active: Boolean) {
    binding.swiperefreshStatistics.isRefreshing = active
  }

  private fun showStatistics(statistics: Statistics) {
    binding.textStatisticsEmpty.visibility = if (statistics.isEmpty) View.VISIBLE else View.GONE
    binding.textStatisticsActive.visibility = if (statistics.isEmpty) View.GONE else View.VISIBLE
    binding.textStatisticsCompleted.visibility = if (statistics.isEmpty) View.GONE else View.VISIBLE

    binding.textStatisticsActive.text = string(R.string.statistics_active_tasks, statistics.active)
    binding.textStatisticsCompleted.text = string(R.string.statistics_completed_tasks, statistics.completed)
  }

  override fun onNavigationIconClick(click: () -> Unit) {
    binding.toolbar.setNavigationOnClickListener { click() }
  }

  fun onSwipeRefresh(swipe: () -> Unit) {
    binding.swiperefreshStatistics.setOnRefreshListener { swipe() }
  }
}

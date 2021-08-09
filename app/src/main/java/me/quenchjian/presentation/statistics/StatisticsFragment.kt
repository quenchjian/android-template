package me.quenchjian.presentation.statistics

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewStaticticsBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.createView
import me.quenchjian.presentation.drawer.DrawerFragment
import me.quenchjian.presentation.drawer.DrawerScreen
import me.quenchjian.presentation.statistics.usecase.CalculateTasksUseCase
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment : DrawerFragment<StatisticsScreen.View>(R.layout.view_statictics),
  StatisticsScreen.Controller {

  @Inject lateinit var calculateTasks: CalculateTasksUseCase

  override val view by createView { StatisticsView(ViewStaticticsBinding.bind(it)) }
  override fun getCurrentMenu() = DrawerScreen.Menu.STATISTICS

  override fun onStart() {
    super.onStart()
    view.onSwipeRefresh { calculate(true) }
    calculate(false)
  }

  override fun calculate(reload: Boolean) {
    calculateTasks.onStart { view.toggleCalculating(true) }
      .onComplete { view.toggleCalculating(false) }
      .onSuccess { view.showStatistics(it) }
      .onError { handleError(it) }
      .invoke(reload)
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment() = StatisticsFragment()
  }
}

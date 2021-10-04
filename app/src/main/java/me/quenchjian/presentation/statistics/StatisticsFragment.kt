package me.quenchjian.presentation.statistics

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewStaticticsBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.drawer.DrawerFragment
import me.quenchjian.presentation.drawer.DrawerScreen
import me.quenchjian.presentation.statistics.usecase.CalculateTasksUseCase
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment : DrawerFragment<StatisticsScreen.View>(R.layout.view_statictics),
  StatisticsScreen.Controller {

  @Inject lateinit var calculateTasks: CalculateTasksUseCase

  override val view by createView { StatisticsView(ViewStaticticsBinding.bind(it)) }
  override val drawer by lazy { view }
  override fun getCurrentMenu() = DrawerScreen.Menu.STATISTICS

  override fun onStart() {
    super.onStart()
    calculateTasks.subscribe(State.Observer(
      onStart = { drawer.toggleCalculating(true) },
      onComplete = { drawer.toggleCalculating(false) },
      onSuccess = { drawer.showStatistics(it) },
      onError = this::handleError
    ))
    drawer.onSwipeRefresh { calculate(true) }
    calculate(false)
  }

  override fun onStop() {
    super.onStop()
    calculateTasks.dispose()
  }

  override fun calculate(reload: Boolean) {
    calculateTasks(reload)
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment() = StatisticsFragment()
  }
}

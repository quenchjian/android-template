package me.quenchjian.presentation.statistics

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import me.quenchjian.R
import me.quenchjian.databinding.ViewStaticticsBinding
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.view.createView
import me.quenchjian.presentation.drawer.DrawerFragment
import me.quenchjian.presentation.drawer.Menu
import me.quenchjian.presentation.statistics.controller.StatisticsController
import me.quenchjian.presentation.statistics.model.CalculateTasksUseCase
import me.quenchjian.presentation.statistics.view.StatisticsView
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment : DrawerFragment<StatisticsView>(R.layout.view_statictics) {

  @Inject lateinit var calculateTasks: CalculateTasksUseCase

  private val view by createView { StatisticsView(ViewStaticticsBinding.bind(it)) }
  private val controller: StatisticsController by viewModels()

  override val drawerView get() = view
  override fun getCurrentMenu() = Menu.STATISTICS

  override fun onStart() {
    super.onStart()
    controller.view = view
    view.onSwipeRefresh { controller.calculate(true) }
    controller.calculate(false)
  }

  override fun onStop() {
    super.onStop()
    controller.view = null
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment(): Fragment = StatisticsFragment()
  }
}

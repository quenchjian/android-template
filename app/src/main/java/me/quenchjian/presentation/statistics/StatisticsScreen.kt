package me.quenchjian.presentation.statistics

import me.quenchjian.presentation.common.Screen
import me.quenchjian.presentation.drawer.DrawerScreen

interface StatisticsScreen {

  interface View : DrawerScreen {

    fun toggleCalculating(active: Boolean)
    fun showStatistics(statistics: Statistics)

    fun onSwipeRefresh(swipe: () -> Unit)
  }

  interface Controller : Screen.Controller<View> {

    fun calculate(reload: Boolean)
  }
}

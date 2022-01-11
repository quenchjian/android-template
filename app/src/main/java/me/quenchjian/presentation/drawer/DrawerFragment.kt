package me.quenchjian.presentation.drawer

import android.os.Bundle
import androidx.annotation.LayoutRes
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.navigator
import me.quenchjian.presentation.statistics.StatisticsFragment
import me.quenchjian.presentation.tasks.TasksFragment
import java.util.*

abstract class DrawerFragment<V : DrawerView>(@LayoutRes id: Int) : KeyedFragment(id) {

  abstract val drawerView: V
  private val targets = EnumMap<Menu, FragmentKey>(Menu::class.java)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    targets[Menu.TASKS] = TasksFragment.Key()
    targets[Menu.STATISTICS] = StatisticsFragment.Key()
  }

  override fun onStart() {
    super.onStart()
    drawerView.onNavigationIconClick { drawerView.toggleDrawer(!drawerView.drawerOpened) }
    drawerView.onActionClick { action ->
      drawerView.toggleDrawer(false)
      if (action == getCurrentMenu()) {
        return@onActionClick
      }
      when (action) {
        Menu.TASKS -> navigator.replaceTop(targets[action]!!)
        Menu.STATISTICS -> navigator.replaceTop(targets[action]!!)
      }
    }
  }

  abstract fun getCurrentMenu(): Menu
}

package me.quenchjian.presentation.drawer

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.presentation.common.BaseFragment
import me.quenchjian.presentation.statistics.StatisticsFragment
import me.quenchjian.presentation.tasks.TasksFragment
import java.util.*

abstract class DrawerFragment<VM : ViewModel, V : DrawerView<VM>>(@LayoutRes id: Int) : BaseFragment<VM, V>(id) {

  abstract val drawerView: V
  private val targets = EnumMap<Menu, FragmentKey>(Menu::class.java)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    targets[Menu.TASKS] = TasksFragment.Key()
    targets[Menu.STATISTICS] = StatisticsFragment.Key()
  }

  @CallSuper
  override fun bindViewCommand() {
    super.bindViewCommand()
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

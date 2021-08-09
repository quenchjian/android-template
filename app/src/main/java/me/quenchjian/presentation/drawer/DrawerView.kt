package me.quenchjian.presentation.drawer

import com.google.android.material.navigation.NavigationView
import me.quenchjian.R

abstract class DrawerView(val navigation: NavigationView) : DrawerScreen {

  override fun onActionClick(click: (action: DrawerScreen.Menu) -> Unit) {
    navigation.setNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_tasks -> click(DrawerScreen.Menu.TASKS)
        R.id.action_statistics -> click(DrawerScreen.Menu.STATISTICS)
      }
      true
    }
  }
}

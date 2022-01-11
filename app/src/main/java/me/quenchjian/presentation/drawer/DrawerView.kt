package me.quenchjian.presentation.drawer

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import me.quenchjian.R
import me.quenchjian.presentation.common.Screen

abstract class DrawerView(val navigation: NavigationView) : Screen.View {

  private val drawer: DrawerLayout = navigation.parent as DrawerLayout
  val drawerOpened: Boolean get() = drawer.isDrawerOpen(navigation)

  fun toggleDrawer(open: Boolean) {
    if (open) drawer.openDrawer(GravityCompat.START) else drawer.closeDrawer(GravityCompat.START)
  }

  abstract fun onNavigationIconClick(click: () -> Unit)

  fun onActionClick(click: (action: Menu) -> Unit) {
    navigation.setNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_tasks -> click(Menu.TASKS)
        R.id.action_statistics -> click(Menu.STATISTICS)
      }
      true
    }
  }
}

package me.quenchjian.presentation.drawer

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import me.quenchjian.R

abstract class DrawerView(val navigation: NavigationView) : DrawerScreen {

  private val drawer: DrawerLayout = navigation.parent as DrawerLayout
  override val drawerOpened: Boolean get() = drawer.isDrawerOpen(navigation)

  override fun toggleDrawer(open: Boolean) {
    if (open) drawer.openDrawer(GravityCompat.START) else drawer.closeDrawer(GravityCompat.START)
  }

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

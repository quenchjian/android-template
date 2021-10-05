package me.quenchjian.presentation.drawer

import me.quenchjian.presentation.common.Screen

interface DrawerScreen : Screen.View {

  val drawerOpened: Boolean
  fun toggleDrawer(open: Boolean)

  fun onNavigationIconClick(click: () -> Unit)
  fun onActionClick(click: (menu: Menu) -> Unit)

  enum class Menu {
    TASKS, STATISTICS
  }
}

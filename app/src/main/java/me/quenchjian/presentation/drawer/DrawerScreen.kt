package me.quenchjian.presentation.drawer

import me.quenchjian.presentation.common.Screen

interface DrawerScreen : Screen.View {

  fun onActionClick(click: (menu: Menu) -> Unit)

  enum class Menu {
    TASKS, STATISTICS
  }
}

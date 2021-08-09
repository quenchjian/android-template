package me.quenchjian.presentation.drawer

import android.os.Bundle
import androidx.annotation.LayoutRes
import dagger.hilt.EntryPoint
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.Navigator
import me.quenchjian.presentation.statistics.StatisticsFragment
import me.quenchjian.presentation.tasks.TasksFragment
import java.util.EnumMap
import javax.inject.Inject

@EntryPoint
abstract class DrawerFragment<V : DrawerScreen>(@LayoutRes id: Int) : KeyedFragment(id) {

  @Inject lateinit var navProvider: Navigator.Provider

  abstract val view: V
  private val targets = EnumMap<DrawerScreen.Menu, FragmentKey>(DrawerScreen.Menu::class.java)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    targets[DrawerScreen.Menu.TASKS] = TasksFragment.Key()
    targets[DrawerScreen.Menu.STATISTICS] = StatisticsFragment.Key()
  }

  override fun onStart() {
    super.onStart()
    view.onActionClick { action ->
      if (action == getCurrentMenu()) {
        return@onActionClick
      }
      val navigator = navProvider.get(this)
      when (action) {
        DrawerScreen.Menu.TASKS -> navigator.goBack()
        DrawerScreen.Menu.STATISTICS -> navigator.goTo(targets[action]!!)
      }
    }
  }

  abstract fun getCurrentMenu(): DrawerScreen.Menu
}

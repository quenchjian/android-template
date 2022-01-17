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
import me.quenchjian.presentation.statistics.view.StatisticsView
import me.quenchjian.presentation.statistics.viewmodel.StatisticsViewModel

@AndroidEntryPoint
class StatisticsFragment : DrawerFragment<StatisticsViewModel, StatisticsView>(R.layout.view_statictics) {

  override val v by createView { StatisticsView(ViewStaticticsBinding.bind(it)) }
  override val vm: StatisticsViewModel by viewModels()

  override val drawerView get() = v
  override fun getCurrentMenu() = Menu.STATISTICS

  override fun bindViewProperty() {
    vm.loading.observe(viewLifecycleOwner) { v.calculating = it }
    vm.statistics.observe(viewLifecycleOwner) { v.statistics = it }
    vm.error.observe(viewLifecycleOwner) { v.error = it }
    vm.calculate(false)
  }

  override fun bindViewCommand() {
    super.bindViewCommand()
    v.onSwipeRefresh { vm.calculate(true) }
  }

  @Parcelize
  class Key : FragmentKey() {
    override fun instantiateFragment(): Fragment = StatisticsFragment()
  }
}

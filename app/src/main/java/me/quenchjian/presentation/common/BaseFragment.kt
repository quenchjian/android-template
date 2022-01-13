package me.quenchjian.presentation.common

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import me.quenchjian.navigation.KeyedFragment
import me.quenchjian.navigation.Navigator
import me.quenchjian.presentation.common.view.MvvmView

/**
 * According to the definition of MVVM architecture in [wikipedia](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel),
 * there should be a binder that communicate between the View and its bound properties in the ViewModel.
 *
 * Therefore fragment is perfectly suitable to this role because it contains both View and ViewModel factory.
 * * View factory: [onCreateView] callback
 * * ViewModel factory: override [getDefaultViewModelProviderFactory]
 *
 * Based on previous reason, fragment is not View or ViewModel in MVVM architecture.
 *
 * However android context is needed to achieve screen navigation on android platform that makes
 * fragment part of ViewModel role, therefore, only allow navigation logic in Fragment as a compromise.
 */
abstract class BaseFragment<VM : ViewModel, V : MvvmView<VM>> : KeyedFragment {

  constructor() : super()
  constructor(@LayoutRes id: Int) : super(id)

  abstract val v: V
  abstract val vm: VM

  private var nav: Navigator? = null
  val navigator: Navigator get() = nav ?: Navigator.Provider.get(requireContext()).get(this).also { nav = it }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    bindViewProperty()
    bindViewCommand()
  }

  @CallSuper
  open fun bindViewProperty() {
    v.initViewModel(vm)
  }

  open fun bindViewCommand() {}
}

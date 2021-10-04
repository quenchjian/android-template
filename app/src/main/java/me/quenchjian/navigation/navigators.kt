package me.quenchjian.navigation

import androidx.fragment.app.Fragment

private var nav: Navigator? = null
val Fragment.navigator: Navigator get() = nav ?: Navigator.Provider.get(requireContext()).get(this).also { nav = it }
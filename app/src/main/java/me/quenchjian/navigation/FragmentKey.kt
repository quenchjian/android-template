package me.quenchjian.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

abstract class FragmentKey(tag: String = "") : NavKey, Parcelable {

  val tag: String = if (tag.isEmpty()) javaClass.name else tag

  fun createFragment(): Fragment {
    val frag = instantiateFragment()
    val args = frag.arguments ?: Bundle()
    args.putParcelable(KEY, this)
    frag.arguments = args
    return frag
  }

  abstract fun instantiateFragment(): Fragment

  open fun onForward(ft: FragmentTransaction, enter: Boolean) {
    ft.setCustomAnimations(0, 0, 0, 0)
  }

  open fun onBackward(ft: FragmentTransaction, enter: Boolean) {
    ft.setCustomAnimations(0, 0, 0, 0)
  }

  open fun onReplace(ft: FragmentTransaction, enter: Boolean) {
    ft.setCustomAnimations(0, 0, 0, 0)
  }

  companion object {
    const val KEY = "fragment-key"
  }
}

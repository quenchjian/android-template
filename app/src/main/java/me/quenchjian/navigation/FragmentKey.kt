package me.quenchjian.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

abstract class FragmentKey(
  tag: String = "",
  val transition: Transitions = Transitions.NONE,
) : NavKey, Parcelable {

  val tag: String = if (tag.isEmpty()) javaClass.name else tag

  fun createFragment(): Fragment {
    val frag = instantiateFragment()
    val args = frag.arguments ?: Bundle()
    args.putParcelable(KEY, this)
    frag.arguments = args
    return frag
  }

  abstract fun instantiateFragment(): Fragment

  companion object {
    const val KEY = "fragment-key"
  }
}

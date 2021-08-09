package me.quenchjian.navigation

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class KeyedFragment : Fragment {
  constructor() : super()
  constructor(@LayoutRes id: Int) : super(id)

  fun <KEY : FragmentKey> getKey(): KEY {
    return checkNotNull(requireArguments().getParcelable(FragmentKey.KEY)) { "The key provided as fragment argument should not be null!" }
  }
}

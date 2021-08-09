package me.quenchjian.presentation.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewDelegate<V : Screen.View>(
  private val fragment: Fragment,
  private val factory: (View) -> V
) : ReadOnlyProperty<Fragment, V> {

  private var view: V? = null
  private var viewState = Bundle()

  init {
    fragment.savedStateRegistry.registerSavedStateProvider(KEY_STATE) { viewState }
    fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
      private val liveDataObserver = Observer<LifecycleOwner> {
        val owner = it ?: return@Observer
        owner.lifecycle.addObserver(ViewObserver())
      }

      override fun onCreate(owner: LifecycleOwner) {
        fragment.viewLifecycleOwnerLiveData.observeForever(liveDataObserver)
      }

      override fun onDestroy(owner: LifecycleOwner) {
        fragment.viewLifecycleOwnerLiveData.removeObserver(liveDataObserver)
      }
    })
  }

  override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
    if (view != null) {
      return view!!
    }
    val lifecycle = thisRef.viewLifecycleOwner.lifecycle
    check(lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
      "Cannot get view when Fragment view are destroyed."
    }
    return factory(thisRef.requireView()).also { view = it }
  }

  companion object {
    private const val KEY_STATE = "key-view-state"
  }

  private inner class ViewObserver : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
      val restored = fragment.savedStateRegistry.consumeRestoredStateForKey(KEY_STATE)
      if (restored != null) {
        viewState = restored
      }
    }

    override fun onStart(owner: LifecycleOwner) {
      view?.attach(viewState)
    }

    override fun onStop(owner: LifecycleOwner) {
      view?.detach(viewState)
    }

    override fun onDestroy(owner: LifecycleOwner) {
      view = null
    }
  }
}

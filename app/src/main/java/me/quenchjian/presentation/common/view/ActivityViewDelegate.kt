package me.quenchjian.presentation.common.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewDelegate<V : MvvmView<*>>(
  private val activity: AppCompatActivity,
  private val factory: (LayoutInflater) -> V
) : ReadOnlyProperty<AppCompatActivity, V> {

  private var view: V? = null
  private var viewState = Bundle()

  init {
    activity.savedStateRegistry.registerSavedStateProvider(KEY_STATE) { viewState }
    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        val restored = activity.savedStateRegistry.consumeRestoredStateForKey(KEY_STATE)
        if (restored != null) {
          viewState = restored
        }
      }

      override fun onStart(owner: LifecycleOwner) {
        view?.attach(viewState)
      }

      override fun onStop(owner: LifecycleOwner) {
        view?.detach(viewState)
        view = null
      }
    })
  }

  override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): V {
    if (view != null) {
      return view!!
    }
    check(thisRef.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
      "Cannot get view when Activity are destroyed."
    }
    return factory(thisRef.layoutInflater).also { view = it }
  }

  companion object {
    private const val KEY_STATE = "key-view-state"
  }
}

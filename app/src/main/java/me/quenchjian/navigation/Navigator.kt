package me.quenchjian.navigation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.Deque
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

class Navigator(
  private var host: FragmentActivity,
  private val containerId: Int,
) {

  private val manager get() = host.supportFragmentManager
  private val stack: Deque<FragmentKey> = LinkedList()

  fun getBackStack(): List<FragmentKey> = LinkedList(stack)

  fun setBackStack(keys: List<FragmentKey>) {
    stack.clear()
    keys.forEach { stack.push(it) }
    if (stack.isNotEmpty()) {
      navigate(Direction.REPLACE, null, stack.peek()!!)
    }
  }

  fun goBack(): Boolean {
    if (stack.isEmpty() || stack.size == 1) {
      return false
    }
    navigate(Direction.BACKWARD, stack.pop(), stack.peek()!!)
    return true
  }

  fun goTo(key: NavKey) {
    if (key is ActivityKey) {
      host.startActivity(key.createIntent(host), key.createOption(host))
    } else if (key is FragmentKey) {
      navigate(Direction.FORWARD, stack.peek(), key)
      stack.push(key)
    }
  }

  fun replaceTop(key: NavKey) {
    check(key is FragmentKey) { "Only FragmentKey can perform replaceTop action" }
    navigate(Direction.REPLACE, stack.pop(), key)
    stack.push(key)
  }

  private fun navigate(direction: Direction, from: FragmentKey?, to: FragmentKey) {
    val transaction = manager.beginTransaction().disallowAddToBackStack()
    if (from != null) {
      val current = manager.findFragmentByTag(from.tag)
      if (current != null) {
        if (!stack.contains(from)) {
          transaction.remove(current)
        } else if (!current.isDetached) {
          transaction.detach(current)
        }
        when (direction) {
          Direction.REPLACE -> current.enterTransition = null
          else -> current.enterTransition = to.transition.exit
        }
      }
    }
    var target = manager.findFragmentByTag(to.tag)
    if (target == null) {
      target = to.createFragment()
      transaction.add(containerId, target, to.tag)
    } else {
      if (target.isRemoving) {
        transaction.replace(containerId, target, to.tag)
      } else if (target.isDetached) {
        transaction.attach(target)
      }
    }
    when (direction) {
      Direction.REPLACE -> target.enterTransition = null
      else -> target.enterTransition = to.transition.enter
    }
    transaction.commitAllowingStateLoss()
  }

  private enum class Direction {
    REPLACE, FORWARD, BACKWARD
  }

  companion object {
    fun init(host: FragmentActivity, containerId: Int, backstack: List<FragmentKey>): Navigator {
      return Provider.get(host).init(host, containerId, backstack)
    }
  }

  @Singleton
  class Provider @Inject constructor() {
    private val navigators = mutableMapOf<String, Navigator>()

    fun init(host: FragmentActivity, containerId: Int, backstack: List<FragmentKey>): Navigator {
      val navigator = navigators.getOrPut(host::class.java.name) { Navigator(host, containerId) }
      if (navigator.host != host) {
        navigator.host = host
      } else {
        navigator.setBackStack(backstack)
      }
      return navigator
    }

    fun get(host: FragmentActivity): Navigator {
      return checkNotNull(navigators[host::class.java.name]) { "Navigator didn't init" }
    }

    fun get(fragment: Fragment): Navigator {
      return checkNotNull(navigators[fragment.requireActivity()::class.java.name]) { "Navigator didn't init" }
    }

    companion object {
      fun get(context: Context): Provider {
        return EntryPointAccessors.fromApplication(context, Entry::class.java).provider()
      }
    }
  }

  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface Entry {
    fun provider(): Provider
  }
}

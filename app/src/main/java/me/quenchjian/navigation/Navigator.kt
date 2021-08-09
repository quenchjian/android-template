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
  private val host: FragmentActivity,
  private val containerId: Int
) {

  private val manager = host.supportFragmentManager
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
      when (direction) {
        Direction.REPLACE -> from.onReplace(transaction, false)
        Direction.FORWARD -> from.onForward(transaction, false)
        Direction.BACKWARD -> from.onBackward(transaction, false)
      }
      val current = manager.findFragmentByTag(from.tag)
      if (current != null) {
        if (!stack.contains(from)) {
          transaction.remove(current)
        } else if (!current.isDetached) {
          transaction.detach(current)
        }
      }
    }
    when (direction) {
      Direction.REPLACE -> to.onReplace(transaction, true)
      Direction.FORWARD -> to.onForward(transaction, true)
      Direction.BACKWARD -> to.onBackward(transaction, true)
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
    transaction.commitAllowingStateLoss()
  }

  private enum class Direction {
    REPLACE, FORWARD, BACKWARD
  }

  @Singleton
  class Provider @Inject constructor() {
    private val navigators = mutableMapOf<String, Navigator>()

    fun get(host: FragmentActivity, containerId: Int): Navigator {
      return navigators.getOrPut(host.javaClass.name) { Navigator(host, containerId) }
    }

    fun get(host: FragmentActivity): Navigator {
      return checkNotNull(navigators[host.javaClass.name]) { "Navigator didn't init" }
    }

    fun get(fragment: Fragment): Navigator {
      return checkNotNull(navigators[fragment.requireActivity().javaClass.name]) { "Navigator didn't init" }
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

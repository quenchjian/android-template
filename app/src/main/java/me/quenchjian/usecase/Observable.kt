package me.quenchjian.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.quenchjian.concurrent.Schedulers
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.CoroutineContext

abstract class Observable(private val scheduler: Schedulers) : CoroutineScope {

  override val coroutineContext: CoroutineContext = scheduler.ui

  protected var startRunner: () -> Unit = {}
  protected var completeRunner: () -> Unit = {}
  protected var errorRunner: (Throwable) -> Unit = {}

  private val lock = ReentrantLock()
  private val observers = LinkedHashSet<StateObserver>()

  fun subscribe(observer: StateObserver) {
    lock.withLock { observers.add(observer) }
  }

  fun unsubscribe(observer: StateObserver) {
    lock.withLock { observers.remove(observer) }
  }

  fun dispose() {
    lock.withLock { observers.clear() }
  }

  protected fun <T> tryInvoke(block: suspend () -> T) {
    launch {
      try {
        notifyStateChange(State.Loading)
        val obj: T = withContext(scheduler.io) { block() }
        notifyStateChange(State.Success(obj))
      } catch (t: Throwable) {
        notifyStateChange(State.Error(t))
      } finally {
        notifyStateChange(State.Complete)
      }
    }
  }

  private fun notifyStateChange(state: State) {
    getObservers().forEach { it.onStateChanged(state) }
  }

  private fun getObservers(): Set<StateObserver> {
    return lock.withLock { observers.toHashSet() }
  }
}

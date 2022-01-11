package me.quenchjian.presentation.common.model

import kotlinx.coroutines.CoroutineScope
import me.quenchjian.concurrent.Schedulers
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.CoroutineContext

abstract class UseCase<T>(protected val scheduler: Schedulers) : CoroutineScope {

  override val coroutineContext: CoroutineContext = scheduler.ui

  private val lock = ReentrantLock()
  private val listeners = LinkedHashSet<T>()

  fun registerListener(listener: T) {
    lock.withLock { listeners.add(listener) }
  }

  fun unregisterListener(listener: T) {
    lock.withLock { listeners.remove(listener) }
  }

  fun dispose() {
    lock.withLock { listeners.clear() }
  }

  protected fun getListeners(): Set<T> {
    return lock.withLock { listeners.toHashSet() }
  }
}

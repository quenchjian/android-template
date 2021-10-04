package me.quenchjian.concurrent

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestScheduler : Schedulers {
  override val io: CoroutineContext = Dispatchers.Unconfined
  override val ui: CoroutineContext = Dispatchers.Unconfined
}

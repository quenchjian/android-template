package me.quenchjian.concurrent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultScheduler @Inject constructor() : Schedulers {

  private val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor

  init {
    executor.corePoolSize = 3
  }

  override val io = executor.asCoroutineDispatcher()
  override val ui = Dispatchers.Main
}

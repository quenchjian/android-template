package me.quenchjian.concurrent

import kotlin.coroutines.CoroutineContext

interface Schedulers {

  val io: CoroutineContext
  val ui: CoroutineContext
}

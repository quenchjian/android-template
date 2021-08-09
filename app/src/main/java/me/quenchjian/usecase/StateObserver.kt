package me.quenchjian.usecase

fun interface StateObserver {

  fun onStateChanged(state: State)
}

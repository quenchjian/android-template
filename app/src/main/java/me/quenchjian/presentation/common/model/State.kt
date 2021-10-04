package me.quenchjian.presentation.common.model

interface State {

  object Loading : State
  object Complete : State
  class Success<T>(val t: T) : State
  class Error(val t: Throwable) : State

  class Observer<T>(
    private val onStart: () -> Unit = {},
    private val onComplete: () -> Unit = {},
    private val onSuccess: (T) -> Unit = {},
    private val onError: (Throwable) -> Unit = {},
  ) {

    @Suppress("unchecked_cast")
    fun onStateChanged(state: State) {
      when (state) {
        is Loading -> onStart.invoke()
        is Complete -> onComplete.invoke()
        is Success<*> -> onSuccess.invoke(state.t as T)
        is Error -> onError.invoke(state.t)
      }
    }
  }
}

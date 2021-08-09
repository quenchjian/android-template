package me.quenchjian.usecase

interface State {

  object Loading : State
  object Complete : State
  class Success<T>(val t: T) : State
  class Error(val t: Throwable) : State
}

package me.quenchjian.presentation.common.controller

import me.quenchjian.presentation.common.view.MvcView
import timber.log.Timber

interface Controller<V : MvcView> {
  var view: V?
  fun handleError(t: Throwable) {
    Timber.e(t)
    view?.showError(t.message ?: "Unknown Error")
  }
}

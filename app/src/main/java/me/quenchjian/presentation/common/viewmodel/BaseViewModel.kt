package me.quenchjian.presentation.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel() {

  protected fun <T> LiveData<T>.mutable(): MutableLiveData<T> = this as MutableLiveData<T>
}

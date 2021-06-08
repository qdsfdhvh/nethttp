package com.seiko.okhttp.flow.vm

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseRxViewModel : ViewModel() {

  private val compositeDisposable = CompositeDisposable()

  protected fun Disposable.addToDisposables() {
    compositeDisposable.add(this)
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.dispose()
  }
}
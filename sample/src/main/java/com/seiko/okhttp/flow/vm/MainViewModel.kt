package com.seiko.okhttp.flow.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seiko.okhttp.flow.ui.Tabs

class MainViewModel : ViewModel() {

  private val _tab = MutableLiveData<Tabs>()
  val tab: LiveData<Tabs> = _tab

  fun setTab(tab: Tabs) {
    _tab.value = tab
  }
}
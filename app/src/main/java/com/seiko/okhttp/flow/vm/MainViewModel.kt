package com.seiko.okhttp.flow.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.seiko.okhttp.flow.ui.Tabs

class MainViewModel : ViewModel() {

  val tab = mutableStateOf(Tabs.RxJava)

  fun setTab(tab: Tabs) {
    this.tab.value = tab
  }
}
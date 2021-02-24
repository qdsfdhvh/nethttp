package com.seiko.okhttp.flow

import android.app.Application
import com.seiko.net.NetHttp
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())

    NetHttp.init(Global.okHttpClient, Global.moshiConverter)
    NetHttp.baseUrl = "https://www.wanandroid.com/"
  }
}
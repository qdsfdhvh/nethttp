package com.seiko.okhttp.flow

import android.app.Application
import com.seiko.net.NetHttp
import com.seiko.net.converter.moshi.MoshiConverter
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())

    NetHttp.init(Global.okHttpClient, MoshiConverter.create(Global.moshi))
    NetHttp.setBaseUrl("https://www.wanandroid.com/")
  }

}
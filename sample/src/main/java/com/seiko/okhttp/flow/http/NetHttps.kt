package com.seiko.okhttp.flow.http

import com.seiko.net.Converter
import com.seiko.net.FlowNetHttp
import com.seiko.net.NetHttp
import com.seiko.net.RxNetHttp
import com.seiko.net.converter.gson.GsonConverter
import com.seiko.okhttp.flow.Global
import com.seiko.okhttp.flow.createLoggingInterceptor
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS

object GsonNetHttp : NetHttp by NetHttp {

  private val converter = GsonConverter.create(Global.gson)

  override fun converter(): Converter = converter
}

object DownloadNetHttp : RxNetHttp, FlowNetHttp, NetHttp by NetHttp {

  private val okHttpClient by lazy {
    OkHttpClient.Builder()
      .addInterceptor(createLoggingInterceptor(HEADERS))
      .build()
  }

  override fun okHttpClient(): OkHttpClient = okHttpClient

  override fun scheduler(): Scheduler = Schedulers.computation()

  override fun dispatcher(): CoroutineDispatcher = Dispatchers.Default
}
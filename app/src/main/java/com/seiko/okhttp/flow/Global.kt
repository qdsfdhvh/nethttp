package com.seiko.okhttp.flow

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.seiko.net.converter.fastjson.FastJsonConverter
import com.seiko.net.converter.gson.GsonConverter
import com.seiko.net.converter.moshi.MoshiConverter
import com.seiko.okhttp.flow.util.createLoggingInterceptor
import com.squareup.moshi.Moshi
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Global {

  val okHttpClient by lazy { createOkHttpClient() }

  val downloadOkHttpClient by lazy { createDownloadOkHttpClient() }

  val moshi by lazy { createMoshi() }

  val gson by lazy { createGson() }

  val moshiConverter by lazy { createMoshiConverter() }

  val gsonConverter by lazy { createGsonConverter() }

  val fastJsonConverter by lazy { createFastJsonConverter() }

  private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(createLoggingInterceptor(BODY))
      .build()
  }

  private fun createDownloadOkHttpClient(): OkHttpClient {
    val executors = Executors.newCachedThreadPool()
    return OkHttpClient.Builder()
      .dispatcher(Dispatcher(executors))
      .addInterceptor(createLoggingInterceptor(HEADERS))
      .connectTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(15, TimeUnit.SECONDS)
      .readTimeout(15, TimeUnit.SECONDS)
      .build()
  }

  private fun createMoshi(): Moshi {
    return Moshi.Builder().build()
  }

  private fun createGson(): Gson {
    return GsonBuilder().create()
  }

  private fun createMoshiConverter(): MoshiConverter {
    return MoshiConverter.create(moshi)
  }

  private fun createGsonConverter(): GsonConverter {
    return GsonConverter.create(gson)
  }

  private fun createFastJsonConverter(): FastJsonConverter {
    return FastJsonConverter.create()
  }
}
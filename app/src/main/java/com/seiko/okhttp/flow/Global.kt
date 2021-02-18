package com.seiko.okhttp.flow

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

object Global {

  val okHttpClient by lazy { createOkHttpClient() }

  val moshi by lazy { createMoshi() }

  val gson by lazy { createGson() }

  private fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor {
      Timber.tag("okHttp").d(it)
    }
    logging.level = if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor.Level.BODY
    } else {
      HttpLoggingInterceptor.Level.NONE
    }
    return OkHttpClient.Builder()
      .addInterceptor(logging)
      .build()
  }

  private fun createMoshi(): Moshi {
    return Moshi.Builder()
      .build()
  }

  private fun createGson(): Gson {
    return GsonBuilder().create()
  }
}
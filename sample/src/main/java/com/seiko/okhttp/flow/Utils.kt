package com.seiko.okhttp.flow

import android.content.Context
import android.os.Environment
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

fun createLoggingInterceptor(
  level: HttpLoggingInterceptor.Level
): Interceptor {
  val logging = HttpLoggingInterceptor {
    Timber.tag("okHttp").d(it)
  }
  logging.level = if (BuildConfig.DEBUG)
    level else HttpLoggingInterceptor.Level.NONE
  return logging
}

fun Context.defaultDownloadDir(): String {
  return getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath
}
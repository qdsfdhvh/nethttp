package com.seiko.net

import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import java.io.IOException

interface NetHttp {

  fun converter(): Converter

  fun okHttpClient(): OkHttpClient

  fun wrapperUrl(url: String): String

  interface Call : NetHttp {
    @WorkerThread
    fun newCall(): okhttp3.Call
  }

  interface Callback<in T> {
    fun onSuccess(data: T)
    fun onFailure(e: IOException)
  }

  companion object : NetHttp {

    var baseUrl: String = ""
      set(value) {
        field = if (value.endsWith('/')) value else "${value}/"
      }

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var converter: Converter

    @JvmStatic
    fun init(okHttpClient: OkHttpClient, converter: Converter) {
      this.okHttpClient = okHttpClient
      this.converter = converter
    }

    override fun converter(): Converter = converter

    override fun okHttpClient(): OkHttpClient = okHttpClient

    override fun wrapperUrl(url: String): String = wrapperUrl(baseUrl, url)
  }
}
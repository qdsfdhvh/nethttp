package com.seiko.net

import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient

interface NetHttp {

  fun converter(): Converter

  fun okHttpClient(): OkHttpClient

  fun wrapperUrl(url: String): String

  interface Call : NetHttp {
    @WorkerThread
    fun newCall(): okhttp3.Call
  }

  companion object : NetHttp {

    private var baseUrl: String = ""

    private lateinit var okHttpClient: OkHttpClient

    private lateinit var converter: Converter

    fun init(okHttpClient: OkHttpClient, converter: Converter) {
      this.okHttpClient = okHttpClient
      this.converter = converter
    }

    fun setBaseUrl(baseUrl: String) {
      this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "${baseUrl}/"
    }

    override fun converter(): Converter = converter

    override fun okHttpClient(): OkHttpClient = okHttpClient

    override fun wrapperUrl(url: String): String {
      if (url.startsWith("http")) return url
      if (url.startsWith("/")) return baseUrl + url.substring(1)
      return "$baseUrl$url"
    }
  }
}
package com.seiko.net

import okhttp3.OkHttpClient

fun NetHttp.setConverter(converter: Converter): NetHttp {
  return object : NetHttp by this {
    override fun converter(): Converter = converter
  }
}

fun NetHttp.setOkHttpClient(okHttpClient: OkHttpClient): NetHttp {
  return object : NetHttp by this {
    override fun okHttpClient(): OkHttpClient = okHttpClient
  }
}
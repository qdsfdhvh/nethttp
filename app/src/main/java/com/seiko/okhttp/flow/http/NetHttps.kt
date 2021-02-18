package com.seiko.okhttp.flow.http

import com.seiko.net.Converter
import com.seiko.net.NetHttp
import com.seiko.net.converter.gson.GsonConverter
import com.seiko.okhttp.flow.Global
import okhttp3.OkHttpClient

object GsonNetHttp : NetHttp {

  private val converter = GsonConverter.create(Global.gson)

  override fun converter(): Converter = converter

  override fun okHttpClient(): OkHttpClient = Global.okHttpClient

  override fun wrapperUrl(url: String): String = NetHttp.wrapperUrl(url)
}

// object KotlinxNetHttp : NetHttp {
//
//   private val converter = KotlinxConverter.create(
//     Json {
//       serializersModule = SerializersModule {
//
//       }
//     }
//   )
//
//   override fun converter(): Converter = converter
//
//   override fun okHttpClient(): OkHttpClient = Global.okHttpClient
//
//   override fun wrapperUrl(url: String): String = NetHttp.wrapperUrl(url)
// }
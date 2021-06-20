package com.seiko.okhttp.flow.http

import com.seiko.net.Converter
import com.seiko.net.NetHttp
import com.seiko.okhttp.flow.Global
import okhttp3.OkHttpClient

object GsonNetHttp : NetHttp by NetHttp {
  override fun converter(): Converter = Global.gsonConverter
}

object DownloadNetHttp : NetHttp by NetHttp {
  override fun okHttpClient(): OkHttpClient = Global.downloadOkHttpClient
}
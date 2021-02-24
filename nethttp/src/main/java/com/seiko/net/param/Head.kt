package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun NetHttp.head(url: String) =
  HeadParamNetHttp(this, url)

class HeadParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
) : AbsHeaderParamNetHttp<HeadParamNetHttp>(netHttp) {
  override fun buildRequest(): Request {
    return Request.Builder()
      .url(wrapperUrl(url).toHttpUrl())
      .head()
      .headers(buildHeaders())
      .build()
  }
}
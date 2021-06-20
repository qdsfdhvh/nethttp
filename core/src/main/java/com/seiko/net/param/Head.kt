package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request

fun NetHttp.head(
  url: String,
  paramsBuilder: HeaderRequestParams.() -> Unit = {},
) = HeadParamNetHttp(this, url, paramsBuilder)

class HeadParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  private val paramsBuilder: HeaderRequestParams.() -> Unit
) : AbsHeaderParamNetHttp(netHttp) {

  private val params = HeaderRequestParams()

  override fun requestParams(): HeaderRequestParams = params

  override fun buildRequest(): Request {
    params.run(paramsBuilder)
    return Request.Builder()
      .url(buildHttpUrl(url))
      .head()
      .headers(buildHeaders())
      .build()
  }
}
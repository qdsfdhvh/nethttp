package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request

class HeadParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  paramsBuilder: HeaderRequestParams.() -> Unit
) : AbsHeaderParamNetHttp<HeadParamNetHttp>(netHttp) {

  private val params = HeaderRequestParams().apply(paramsBuilder)

  override fun requestParams(): HeaderRequestParams = params

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .head()
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.head(
  url: String,
  paramsBuilder: HeaderRequestParams.() -> Unit = {},
): HeadParamNetHttp {
  return HeadParamNetHttp(this, url, paramsBuilder)
}
package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request

class GetParamFlowHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  paramsBuilder: HeaderRequestParams.() -> Unit
) : AbsHeaderParamNetHttp<GetParamFlowHttp>(netHttp) {

  private val params = HeaderRequestParams().apply(paramsBuilder)

  override fun requestParams(): HeaderRequestParams = params

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .get()
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.get(
  url: String,
  paramsBuilder: HeaderRequestParams.() -> Unit = {},
): GetParamFlowHttp {
  return GetParamFlowHttp(this, url, paramsBuilder)
}
package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request

class GetParamFlowHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
) : AbsHeaderParamNetHttp<GetParamFlowHttp>(netHttp) {
  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .get()
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.get(url: String): GetParamFlowHttp {
  return GetParamFlowHttp(this, url)
}
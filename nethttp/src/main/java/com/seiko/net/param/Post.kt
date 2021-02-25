package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  private var bodyBinder: (() -> RequestBody)?,
) : AbsHeaderParamNetHttp<PostParamNetHttp>(netHttp) {

  fun setBody(bodyBinder: (() -> RequestBody)?) = apply {
    this.bodyBinder = bodyBinder
  }

  override fun buildRequest(): Request {
    val httpUrl = wrapperUrl(url).toHttpUrl()
    return Request.Builder()
      .url(httpUrl)
      .post(bodyBinder?.invoke() ?: byteArrayOf().toRequestBody())
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.post(
  url: String,
  bodyBinder: (() -> RequestBody)? = null
): PostParamNetHttp {
  return PostParamNetHttp(this, url, bodyBinder)
}

package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun NetHttp.postJson(url: String) = PostJsonParamNetHttp(this, url)

class PostJsonParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
) : AbsHeaderParamNetHttp<PostJsonParamNetHttp>(netHttp) {

  // body = selfBody ?: mapBody
  private var selfBody: Any? = null
  private val mapBody = hashMapOf<String, Any>()

  fun body(body: Any?) = apply {
    selfBody = body
  }

  fun add(key: String, value: Any) = apply {
    mapBody[key] = value
  }

  fun addAll(map: Map<String, Any>) = apply {
    mapBody.putAll(map)
  }

  override fun buildRequest(): Request {
    val httpUrl = wrapperUrl(url).toHttpUrl()
    val body = selfBody ?: mapBody
    val requestBody = converter().convert(body, body::class.java)
    return Request.Builder()
      .url(httpUrl)
      .post(requestBody)
      .headers(buildHeaders())
      .build()
  }
}
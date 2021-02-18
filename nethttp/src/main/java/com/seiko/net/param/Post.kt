package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun NetHttp.post(url: String, vararg args: Any) =
  post(String.format(url, args))

fun NetHttp.post(url: String) =
  PostParamFlowHttp(this, url = url)

class PostParamFlowHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
) : AbsParamNetHttp(netHttp) {

  // body = selfBody ?: mapBody
  private var selfBody: Any? = null
  private val mapBody = hashMapOf<String, Any>()

  private val headersBuilder = Headers.Builder()

  fun body(body: Any?) = apply { selfBody = body }

  fun add(key: String, value: Any) = apply { mapBody[key] = value }

  fun addAll(map: Map<String, Any>) = apply { mapBody.putAll(map) }

  fun addHeader(name: String, value: String) = apply { headersBuilder.add(name, value) }

  override fun buildRequest(): Request {
    val httpUrl = wrapperUrl(url).toHttpUrl()

    val selfBody = selfBody
    val requestBody = if (selfBody != null)
      converter().convert(selfBody, selfBody::class.java)
    else
      converter().convert(mapBody, HashMap::class.java)

    return Request.Builder()
      .url(httpUrl)
      .post(requestBody)
      .headers(headersBuilder.build())
      .build()
  }
}
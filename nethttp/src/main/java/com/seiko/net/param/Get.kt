package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun NetHttp.get(url: String, vararg args: Any) =
  get(String.format(url, args))

fun NetHttp.get(url: String) =
  GetParamFlowHttp(this, url = url)

class GetParamFlowHttp internal constructor(
  netHttp: NetHttp,
  private val url: String
) : AbsParamNetHttp(netHttp) {

  private val queryParams = hashSetOf<QueryParam>()

  private val headersBuilder = Headers.Builder()

  fun add(key: String, value: Any) = apply { addQuery(key, value, false) }

  fun addEncode(key: String, value: Any) = apply { addQuery(key, value, true) }

  fun addHeader(name: String, value: String) = apply { headersBuilder.add(name, value) }

  private fun addQuery(key: String, value: Any, encode: Boolean) = apply {
    queryParams.add(QueryParam(key, value, encode))
  }

  override fun buildRequest(): Request {
    var httpUrl = wrapperUrl(url).toHttpUrl()
    if (queryParams.isNotEmpty()) {
      httpUrl = httpUrl.newBuilder()
        .addQueryParameters(queryParams)
        .build()
    }
    return Request.Builder()
      .url(httpUrl)
      .get()
      .headers(headersBuilder.build())
      .build()
  }
}

private fun HttpUrl.Builder.addQueryParameters(
  queryParams: Set<QueryParam>
): HttpUrl.Builder {
  queryParams.forEach { param ->
    if (param.encode) {
      addEncodedQueryParameter(param.key, param.value.toString())
    } else {
      addQueryParameter(param.key, param.value.toString())
    }
  }
  return this
}

private data class QueryParam(
  val key: String,
  val value: Any,
  val encode: Boolean = false,
) {

  override fun hashCode(): Int {
    return key.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as QueryParam
    if (key != other.key) return false
    if (value != other.value) return false
    if (encode != other.encode) return false
    return true
  }
}
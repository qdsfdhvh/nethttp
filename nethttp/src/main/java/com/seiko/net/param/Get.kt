package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.model.KeyValue
import com.seiko.net.util.addQueryParameters
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

class GetParamsUrl {
  private val params = hashSetOf<KeyValue>()

  fun add(key: String, value: Any, encode: Boolean = false) {
    params.add(KeyValue(key, value, encode))
  }

  fun addEncode(key: String, value: Any) {
    add(key, value, true)
  }

  fun addAll(options: Map<String, Any?>, encode: Boolean = false) {
    options.forEach { entry -> add(entry.key, entry.value ?: "", encode) }
  }

  internal fun buildHttpUrl(url: String): HttpUrl {
    var httpUrl = url.toHttpUrl()
    if (params.isNotEmpty()) {
      httpUrl = httpUrl.newBuilder()
        .addQueryParameters(params)
        .build()
    }
    return httpUrl
  }
}

class GetParamFlowHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  params: GetParamsUrl.() -> Unit
) : AbsHeaderParamNetHttp<GetParamFlowHttp>(netHttp) {

  private val config = GetParamsUrl().apply(params)

  fun add(key: String, value: Any, encode: Boolean = false) = apply {
    config.add(key, value, encode)
  }

  fun addEncode(key: String, value: Any) = apply {
    config.addEncode(key, value)
  }

  fun addAll(options: Map<String, Any?>, encode: Boolean = false) = apply {
    config.addAll(options, encode)
  }

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(config.buildHttpUrl(wrapperUrl(url)))
      .get()
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.get(
  url: String,
  params: GetParamsUrl.() -> Unit = {}
): GetParamFlowHttp {
  return GetParamFlowHttp(this, url, params)
}
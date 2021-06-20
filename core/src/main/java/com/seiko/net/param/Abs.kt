package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.internal.KeyValue
import com.seiko.net.internal.util.addQueryParameters
import okhttp3.Call
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import java.time.Instant
import java.util.Date

interface ParamNetHttp : NetHttp.Call {
  fun buildRequest(): Request
}

abstract class AbsParamNetHttp(netHttp: NetHttp) : ParamNetHttp, NetHttp by netHttp {
  override fun newCall(): Call = okHttpClient().newCall(buildRequest())
}

abstract class AbsHeaderParamNetHttp(netHttp: NetHttp) : AbsParamNetHttp(netHttp) {

  abstract fun requestParams(): HeaderRequestParams

  protected open fun buildHttpUrl(url: String): HttpUrl {
    var httpUrl = wrapperUrl(url).toHttpUrl()
    val queryParams = requestParams().queryParams
    if (queryParams.isNotEmpty()) {
      httpUrl = httpUrl.newBuilder()
        .addQueryParameters(queryParams)
        .build()
    }
    return httpUrl
  }

  protected open fun buildHeaders(): Headers {
    return requestParams().headersBuilder.build()
  }
}

open class HeaderRequestParams {

  internal val queryParams = hashSetOf<KeyValue>()
  internal val headersBuilder = Headers.Builder()

  fun addQuery(key: String, value: Any, encode: Boolean = false) {
    queryParams.add(KeyValue(key, value, encode))
  }

  fun addEncodedQuery(key: String, value: Any) {
    queryParams.add(KeyValue(key, value, true))
  }

  fun addHeader(name: String, value: Date) {
    headersBuilder.add(name, value)
  }

  fun addHeader(name: String, value: Instant) {
    headersBuilder.add(name, value)
  }

  fun addHeader(name: String, value: String) {
    headersBuilder.add(name, value)
  }

  fun addHeaders(headers: Map<String, String>) {
    headers.forEach { entry ->
      headersBuilder.add(entry.key, entry.value)
    }
  }

  fun header(name: String, value: Date) {
    headersBuilder[name] = value
  }

  fun header(name: String, value: Instant) {
    headersBuilder[name] = value
  }

  fun header(name: String, value: String) {
    headersBuilder[name] = value
  }
}
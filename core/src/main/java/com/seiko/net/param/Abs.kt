package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.model.KeyValue
import com.seiko.net.util.addQueryParameters
import okhttp3.Call
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import java.time.Instant
import java.util.*

interface ParamNetHttp : NetHttp.Call {
  fun buildRequest(): Request
}

abstract class AbsParamNetHttp(netHttp: NetHttp) : ParamNetHttp, NetHttp by netHttp {
  override fun newCall(): Call = okHttpClient().newCall(buildRequest())
}

@Suppress("UNCHECKED_CAST")
abstract class AbsHeaderParamNetHttp<out T : AbsParamNetHttp>(netHttp: NetHttp) :
  AbsParamNetHttp(netHttp) {

  fun addQuery(key: String, value: Any, encode: Boolean = false): T {
    requestParams().addQuery(key, value, encode)
    return this as T
  }

  fun addEncodedQuery(key: String, value: Any): T {
    requestParams().addEncodedQuery(key, value)
    return this as T
  }

  fun addHeader(name: String, value: Date): T {
    requestParams().addHeader(name, value)
    return this as T
  }

  fun addHeader(name: String, value: Instant): T {
    requestParams().addHeader(name, value)
    return this as T
  }

  fun addHeader(name: String, value: String): T {
    requestParams().addHeader(name, value)
    return this as T
  }

  fun addHeaders(headers: Map<String, String>): T {
    requestParams().addHeaders(headers)
    return this as T
  }

  fun header(name: String, value: Date): T {
    requestParams().header(name, value)
    return this as T
  }

  fun header(name: String, value: Instant): T {
    requestParams().header(name, value)
    return this as T
  }

  fun header(name: String, value: String): T {
    requestParams().header(name, value)
    return this as T
  }

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
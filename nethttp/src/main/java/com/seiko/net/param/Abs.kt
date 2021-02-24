package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Call
import okhttp3.Headers
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

  private val headersBuilder = Headers.Builder()

  fun addHeader(name: String, value: Date): T {
    headersBuilder.add(name, value)
    return this as T
  }

  fun addHeader(name: String, value: Instant): T {
    headersBuilder.add(name, value)
    return this as T
  }

  fun addHeader(name: String, value: String): T {
    headersBuilder.add(name, value)
    return this as T
  }

  fun addHeaders(headers: Map<String, String>): T {
    headers.forEach { entry ->
      headersBuilder.add(entry.key, entry.value)
    }
    return this as T
  }

  protected open fun buildHeaders(): Headers {
    return headersBuilder.build()
  }
}
package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.convert
import okhttp3.Request

fun NetHttp.postJson(
  url: String,
  paramsBuilder: PostJsonRequestParams.() -> Unit = {}
) = PostJsonParamNetHttp(this, url, paramsBuilder)

class PostJsonParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  private val paramsBuilder: PostJsonRequestParams.() -> Unit
) : AbsHeaderParamNetHttp(netHttp) {

  private val params = PostJsonRequestParams()

  override fun requestParams(): HeaderRequestParams = params

  override fun buildRequest(): Request {
    params.run(paramsBuilder)
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(converter().convert(params.buildBody()))
      .headers(buildHeaders())
      .build()
  }
}

class PostJsonRequestParams : HeaderRequestParams() {

  // body = selfBody ?: mapBody
  private var selfBody: Any? = null
  private val mapBody = hashMapOf<String, Any?>()

  fun body(body: Any?) {
    selfBody = body
  }

  fun add(key: String, value: Any) {
    mapBody[key] = value
  }

  fun addAll(map: Map<String, Any?>) {
    mapBody.putAll(map)
  }

  internal fun buildBody() = selfBody ?: mapBody
}
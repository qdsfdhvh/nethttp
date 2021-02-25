package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.util.convert
import okhttp3.Request

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

class PostJsonParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  paramsBuilder: PostJsonRequestParams.() -> Unit
) : AbsHeaderParamNetHttp<PostJsonParamNetHttp>(netHttp) {

  private val params = PostJsonRequestParams().apply(paramsBuilder)

  override fun requestParams(): HeaderRequestParams = params

  fun body(body: Any?) = apply {
    params.body(body)
  }

  fun add(key: String, value: Any) = apply {
    params.add(key, value)
  }

  fun addAll(map: Map<String, Any?>) = apply {
    params.addAll(map)
  }

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(converter().convert(params.buildBody()))
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.postJson(
  url: String,
  paramsBuilder: PostJsonRequestParams.() -> Unit = {}
): PostJsonParamNetHttp {
  return PostJsonParamNetHttp(this, url, paramsBuilder)
}

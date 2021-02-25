package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.util.convert
import okhttp3.Request

class PostJsonBody {

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

  internal fun build() = selfBody ?: mapBody
}

class PostJsonParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  body: PostJsonBody.() -> Unit
) : AbsHeaderParamNetHttp<PostJsonParamNetHttp>(netHttp) {

  private val bodyBuilder = PostJsonBody().apply(body)

  fun body(body: Any?) = apply {
    bodyBuilder.body(body)
  }

  fun add(key: String, value: Any) = apply {
    bodyBuilder.add(key, value)
  }

  fun addAll(map: Map<String, Any?>) = apply {
    bodyBuilder.addAll(map)
  }

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(converter().convert(bodyBuilder.build()))
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.postJson(
  url: String,
  body: PostJsonBody.() -> Unit = {}
): PostJsonParamNetHttp {
  return PostJsonParamNetHttp(this, url, body)
}

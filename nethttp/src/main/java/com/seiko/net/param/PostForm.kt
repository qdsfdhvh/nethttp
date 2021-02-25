package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.model.KeyValue
import com.seiko.net.util.addParams
import okhttp3.FormBody
import okhttp3.Request

class PostFormBody {
  private val params = hashSetOf<KeyValue>()

  fun add(key: String, value: Any, encode: Boolean = false) {
    params.add(KeyValue(key, value, encode))
  }

  fun addEncode(key: String, value: Any) {
    params.add(KeyValue(key, value, true))
  }

  internal fun build(): FormBody {
    return FormBody.Builder()
      .addParams(params)
      .build()
  }
}

class PostFormParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  body: PostFormBody.() -> Unit = {}
) : AbsHeaderParamNetHttp<PostFormParamNetHttp>(netHttp) {

  private val bodyBuilder = PostFormBody().apply(body)

  fun add(key: String, value: Any, encode: Boolean = false) = apply {
    bodyBuilder.add(key, value, encode)
  }

  fun addEncode(key: String, value: Any) = apply {
    bodyBuilder.addEncode(key, value)
  }

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(bodyBuilder.build())
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.postForm(
  url: String,
  body: PostFormBody.() -> Unit = {}
): PostFormParamNetHttp {
  return PostFormParamNetHttp(this, url, body)
}
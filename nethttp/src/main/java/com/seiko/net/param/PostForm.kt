package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.model.KeyValue
import com.seiko.net.util.addParams
import okhttp3.FormBody
import okhttp3.Request

class PostFormRequestParams : HeaderRequestParams() {

  private val params = hashSetOf<KeyValue>()

  fun add(key: String, value: Any, encode: Boolean = false) {
    params.add(KeyValue(key, value, encode))
  }

  fun addEncode(key: String, value: Any) {
    params.add(KeyValue(key, value, true))
  }

  internal fun buildBody(): FormBody {
    return FormBody.Builder()
      .addParams(params)
      .build()
  }
}

class PostFormParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  paramsBuilder: PostFormRequestParams.() -> Unit = {}
) : AbsHeaderParamNetHttp<PostFormParamNetHttp>(netHttp) {

  private val params = PostFormRequestParams().apply(paramsBuilder)

  override fun requestParams(): HeaderRequestParams = params

  fun add(key: String, value: Any, encode: Boolean = false) = apply {
    params.add(key, value, encode)
  }

  fun addEncode(key: String, value: Any) = apply {
    params.addEncode(key, value)
  }

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(params.buildBody())
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.postForm(
  url: String,
  paramsBuilder: PostFormRequestParams.() -> Unit = {}
): PostFormParamNetHttp {
  return PostFormParamNetHttp(this, url, paramsBuilder)
}
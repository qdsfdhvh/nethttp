package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.internal.KeyValue
import com.seiko.net.internal.util.addParams
import okhttp3.FormBody
import okhttp3.Request

fun NetHttp.postForm(
  url: String,
  paramsBuilder: PostFormRequestParams.() -> Unit = {}
) = PostFormParamNetHttp(this, url, paramsBuilder)

class PostFormParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  private val paramsBuilder: PostFormRequestParams.() -> Unit = {}
) : AbsHeaderParamNetHttp(netHttp) {

  private val params = PostFormRequestParams()

  override fun requestParams(): HeaderRequestParams = params

  override fun buildRequest(): Request {
    params.run(paramsBuilder)
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(params.buildBody())
      .headers(buildHeaders())
      .build()
  }
}

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
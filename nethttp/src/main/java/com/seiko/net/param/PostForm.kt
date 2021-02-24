package com.seiko.net.param

import com.seiko.net.NetHttp
import com.seiko.net.model.KeyValue
import com.seiko.net.util.addParams
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun NetHttp.postForm(url: String) = PostFormParamNetHttp(this, url)

class PostFormParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
) : AbsHeaderParamNetHttp<PostFormParamNetHttp>(netHttp) {

  private val params = hashSetOf<KeyValue>()

  fun add(key: String, value: Any) = apply {
    add(key, value, false)
  }

  fun addEncode(key: String, value: Any) = apply {
    add(key, value, true)
  }

  private fun add(key: String, value: Any, encode: Boolean) {
    params.add(KeyValue(key, value, encode))
  }

  override fun buildRequest(): Request {
    val httpUrl = wrapperUrl(url).toHttpUrl()
    val body = FormBody.Builder()
      .addParams(params)
      .build()
    return Request.Builder()
      .url(httpUrl)
      .post(body)
      .headers(buildHeaders())
      .build()
  }
}
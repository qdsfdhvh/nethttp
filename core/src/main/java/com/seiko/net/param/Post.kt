package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun NetHttp.post(
  url: String,
  paramsBuilder: PostRequestParams.() -> Unit = {},
) = PostParamNetHttp(this, url, paramsBuilder)

class PostParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  private val paramsBuilder: PostRequestParams.() -> Unit,
) : AbsHeaderParamNetHttp(netHttp) {

  private val params = PostRequestParams()

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

class PostRequestParams : HeaderRequestParams() {

  private var bodyBinder: (() -> RequestBody)? = null

  fun setBody(bodyBinder: (() -> RequestBody)?) {
    this.bodyBinder = bodyBinder
  }

  internal fun buildBody(): RequestBody {
    return bodyBinder?.invoke() ?: byteArrayOf().toRequestBody()
  }
}

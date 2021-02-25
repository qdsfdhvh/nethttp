package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostRequestParams : HeaderRequestParams() {

  private var bodyBinder: (() -> RequestBody)? = null

  fun setBody(bodyBinder: (() -> RequestBody)?) {
    this.bodyBinder = bodyBinder
  }

  internal fun buildBody(): RequestBody {
    return bodyBinder?.invoke() ?: byteArrayOf().toRequestBody()
  }
}

class PostParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  paramsBuilder: PostRequestParams.() -> Unit,
) : AbsHeaderParamNetHttp<PostParamNetHttp>(netHttp) {

  private val params = PostRequestParams().apply(paramsBuilder)

  override fun requestParams(): HeaderRequestParams = params

  fun setBody(bodyBinder: (() -> RequestBody)?) = apply {
    params.setBody(bodyBinder)
  }

  override fun buildRequest(): Request {
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(params.buildBody())
      .headers(buildHeaders())
      .build()
  }
}

fun NetHttp.post(
  url: String,
  paramsBuilder: PostRequestParams.() -> Unit = {},
): PostParamNetHttp {
  return PostParamNetHttp(this, url, paramsBuilder)
}

fun NetHttp.post(
  url: String,
  bodyBinder: (() -> RequestBody)? = null
): PostParamNetHttp {
  return post(
    url = url,
    paramsBuilder = { setBody(bodyBinder) }
  )
}

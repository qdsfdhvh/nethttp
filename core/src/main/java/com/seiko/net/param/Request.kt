package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Request

fun NetHttp.request(request: Request): AbsParamNetHttp {
  return object : AbsParamNetHttp(this) {
    override fun buildRequest(): Request = request
  }
}
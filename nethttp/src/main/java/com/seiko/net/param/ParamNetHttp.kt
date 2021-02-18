package com.seiko.net.param

import com.seiko.net.NetHttp
import okhttp3.Call
import okhttp3.Request

interface ParamNetHttp : NetHttp.Call {
  fun buildRequest(): Request
}

abstract class AbsParamNetHttp(
  private val netHttp: NetHttp
) : ParamNetHttp, NetHttp by netHttp {
  override fun newCall(): Call = okHttpClient().newCall(buildRequest())
}
package com.seiko.net

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

private val DEFAULT_DISPATCHER = Dispatchers.IO

interface FlowNetHttp : NetHttp {

  fun dispatcher(): CoroutineDispatcher

  companion object : FlowNetHttp, NetHttp by NetHttp {
    override fun dispatcher(): CoroutineDispatcher = DEFAULT_DISPATCHER
  }
}

fun NetHttp.dispatcher(): CoroutineDispatcher {
  if (this is FlowNetHttp) {
    return dispatcher()
  }
  return DEFAULT_DISPATCHER
}
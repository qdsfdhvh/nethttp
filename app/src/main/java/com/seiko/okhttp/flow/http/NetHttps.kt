package com.seiko.okhttp.flow.http

import com.seiko.net.Converter
import com.seiko.net.FlowNetHttp
import com.seiko.net.NetHttp
import com.seiko.net.RxNetHttp
import com.seiko.okhttp.flow.Global
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

object GsonNetHttp : NetHttp by NetHttp {
  override fun converter(): Converter = Global.gsonConverter
}

object DownloadNetHttp : NetHttp by NetHttp {
  override fun okHttpClient(): OkHttpClient = Global.downloadOkHttpClient
}

object MyRxNetHttp : RxNetHttp, NetHttp by NetHttp {
  override fun scheduler(): Scheduler = Schedulers.computation()
}

object MyFlowNetHttp : FlowNetHttp, NetHttp by NetHttp {
  override fun dispatcher(): CoroutineDispatcher = Dispatchers.Default
}
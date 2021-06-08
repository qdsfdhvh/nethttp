package com.seiko.net

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

private val DEFAULT_SCHEDULER = Schedulers.io()

interface RxNetHttp : NetHttp {

  fun scheduler(): Scheduler

  companion object : RxNetHttp, NetHttp by NetHttp {
    override fun scheduler(): Scheduler = DEFAULT_SCHEDULER
  }
}

fun NetHttp.scheduler(): Scheduler {
  if (this is RxNetHttp) {
    return scheduler()
  }
  return DEFAULT_SCHEDULER
}

fun NetHttp.setScheduler(scheduler: Scheduler): RxNetHttp {
  return object : RxNetHttp, NetHttp by this {
    override fun scheduler(): Scheduler = scheduler
  }
}
package com.seiko.net.download

import com.seiko.net.NetHttp
import com.seiko.net.asSingleOkResponse
import com.seiko.net.param.get
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

fun NetHttp.downloadRx(
  url: String,
  savePath: String,
  saveName: String = "",
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  downloader: RxDownloader = NormalRxDownloader(),
  scheduler: Scheduler = Schedulers.io(),
): Flowable<Progress> = downloadRx(
  task = Task(url, savePath, saveName),
  headers = headers,
  downloader = downloader,
  scheduler = scheduler,
)

fun NetHttp.downloadRx(
  task: Task,
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  downloader: RxDownloader = NormalRxDownloader(),
  scheduler: Scheduler = Schedulers.io()
): Flowable<Progress> = get(task.url)
  .addHeaders(headers)
  .asSingleOkResponse(scheduler)
  .flatMapPublisher { response ->
    downloader.download(task, response)
  }

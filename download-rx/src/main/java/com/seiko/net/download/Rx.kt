package com.seiko.net.download

import com.seiko.net.NetHttp
import io.reactivex.rxjava3.core.Flowable

fun NetHttp.downloadRx(
  url: String,
  savePath: String,
  saveName: String = "",
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  maxConCurrency: Int = DEFAULT_MAX_CONCURRENCY,
  rangeSize: Long = DEFAULT_RANGE_SIZE,
  downloader: RxDownloader = RxDownloadProxy,
): Flowable<Progress> =
  downloadRx(
    task = Task(url, savePath, saveName),
    headers = headers,
    maxConCurrency = maxConCurrency,
    rangeSize = rangeSize,
    downloader = downloader,
  )

fun NetHttp.downloadRx(
  task: Task,
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  maxConCurrency: Int = DEFAULT_MAX_CONCURRENCY,
  rangeSize: Long = DEFAULT_RANGE_SIZE,
  downloader: RxDownloader = RxDownloadProxy,
): Flowable<Progress> {
  require(rangeSize > 1024 * 1024) { "rangeSize must be greater than 1M" }
  val taskInfo = TaskInfo(
    task = task,
    maxConCurrency = maxConCurrency,
    rangeSize = rangeSize,
    netHttp = this
  )
  return downloader.get(taskInfo, headers)
    .flatMapPublisher { response ->
      downloader.download(taskInfo, response)
    }
}

package com.seiko.net.download

import com.seiko.net.NetHttp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapConcat

fun NetHttp.downloadFlow(
  url: String,
  savePath: String,
  saveName: String = "",
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  maxConCurrency: Int = DEFAULT_MAX_CONCURRENCY,
  rangeSize: Long = DEFAULT_RANGE_SIZE,
  isClearCache: Boolean = false,
  downloader: FlowDownloader = FlowDownloaderProxy,
): Flow<Progress> =
  downloadFlow(
    task = Task(url, savePath, saveName),
    headers = headers,
    maxConCurrency = maxConCurrency,
    rangeSize = rangeSize,
    isClearCache = isClearCache,
    downloader = downloader,
  )

@OptIn(FlowPreview::class)
fun NetHttp.downloadFlow(
  task: Task,
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  maxConCurrency: Int = DEFAULT_MAX_CONCURRENCY,
  rangeSize: Long = DEFAULT_RANGE_SIZE,
  isClearCache: Boolean = false,
  downloader: FlowDownloader = FlowDownloaderProxy,
): Flow<Progress> {
  require(rangeSize > 1024 * 1024) { "rangeSize must be greater than 1M" }
  val taskInfo = TaskInfo(
    task = task,
    maxConCurrency = maxConCurrency,
    rangeSize = rangeSize,
    isClearCache = isClearCache,
    netHttp = this
  )
  return downloader.get(taskInfo, headers)
    .flatMapConcat { response ->
      downloader.download(taskInfo, response)
    }
    .conflate()
}
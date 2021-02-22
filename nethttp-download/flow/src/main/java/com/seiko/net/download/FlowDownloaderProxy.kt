package com.seiko.net.download

import com.seiko.net.download.util.isSupportRange
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

object FlowDownloaderProxy : FlowDownloader {
  override fun download(taskInfo: TaskInfo, response: Response): Flow<Progress> =
    when {
      taskInfo.maxConCurrency > 2
        && response.isSupportRange() -> RangeFlowDownloader()
      else -> NormalFlowDownloader()
    }.download(taskInfo, response)
}
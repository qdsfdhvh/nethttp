package com.seiko.net.download

import com.seiko.net.download.util.isSupportRange
import io.reactivex.rxjava3.core.Flowable
import okhttp3.Response

object RxDownloadProxy : RxDownloader {
  override fun download(taskInfo: TaskInfo, response: Response): Flowable<Progress> {
    return when {
      taskInfo.maxConCurrency > 2 && response.isSupportRange() -> RangeRxDownloader()
      else -> NormalRxDownloader()
    }.download(taskInfo, response)
  }
}
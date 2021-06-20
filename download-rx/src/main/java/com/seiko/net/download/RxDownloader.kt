package com.seiko.net.download

import com.seiko.net.asSingleOkResponse
import com.seiko.net.param.get
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import okhttp3.Response

interface RxDownloader {

  fun download(taskInfo: TaskInfo, response: Response): Flowable<Progress>

  fun get(taskInfo: TaskInfo, headers: Map<String, String>): Single<Response> {
    return taskInfo.netHttp
      .get(taskInfo.task.url) {
        addHeaders(headers)
      }
      .asSingleOkResponse()
  }
}

package com.seiko.net.download

import com.seiko.net.asFlowOkResponse
import com.seiko.net.param.get
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface FlowDownloader {

  fun download(taskInfo: TaskInfo, response: Response): Flow<Progress>

  fun get(taskInfo: TaskInfo, headers: Map<String, String>): Flow<Response> {
    return taskInfo.netHttp.get(taskInfo.task.url)
      .addHeaders(headers)
      .asFlowOkResponse()
  }
}
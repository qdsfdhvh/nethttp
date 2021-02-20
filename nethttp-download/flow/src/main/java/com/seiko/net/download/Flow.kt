package com.seiko.net.download

import com.seiko.net.NetHttp
import com.seiko.net.asFlowOkResponse
import com.seiko.net.param.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn

fun NetHttp.downloadFlow(
  url: String,
  savePath: String,
  saveName: String = "",
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  downloader: FlowDownloader = NormalFlowDownloader(),
  dispatcher: CoroutineDispatcher = Dispatchers.IO,
): Flow<Progress> = downloadFlow(
  task = Task(url, savePath, saveName),
  headers = headers,
  downloader = downloader,
  dispatcher = dispatcher,
)

@OptIn(FlowPreview::class)
fun NetHttp.downloadFlow(
  task: Task,
  headers: Map<String, String> = RANGE_CHECK_HEADER,
  downloader: FlowDownloader = NormalFlowDownloader(),
  dispatcher: CoroutineDispatcher = Dispatchers.IO,
): Flow<Progress> = get(task.url)
  .addHeaders(headers)
  .asFlowOkResponse(dispatcher)
  .flatMapConcat { response ->
    downloader.download(task, response)
  }
  .conflate()
  .flowOn(dispatcher)

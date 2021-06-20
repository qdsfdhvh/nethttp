package com.seiko.net.download

import com.seiko.net.download.util.shadow
import com.seiko.net.throwIfFatal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import okio.buffer
import okio.sink

class NormalFlowDownloader : DownloaderDelegate(), FlowDownloader {

  override fun download(taskInfo: TaskInfo, response: Response): Flow<Progress> {
    val body = response.throwIfFatal()

    val task = taskInfo.task
    file = task.getFile()
    shadowFile = file.shadow()

    beforeDownload(task, response, taskInfo.isClearCache)

    val totalSize = response.headersContentLength()
    if (alreadyDownloaded) {
      return flowOf(Progress(totalSize, totalSize))
    }
    return startDownload(body, Progress(totalSize))
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  private fun startDownload(body: ResponseBody, progress: Progress): Flow<Progress> {
    return flow {
      InternalState(
        body.source(),
        shadowFile.sink().buffer()
      ).apply {

        var readLean = source.read(buffer, DEFAULT_BUFFER_SIZE)
        while (readLean != -1L) {
          sink.emit()
          emit(progress.apply {
            downloadSize += readLean
          })
          readLean = source.read(buffer, DEFAULT_BUFFER_SIZE)
        }
        sink.flush()
        shadowFile.renameTo(file)

        sink.closeQuietly()
        source.closeQuietly()
        body.closeQuietly()
      }
    }
  }
}
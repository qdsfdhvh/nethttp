package com.seiko.net.download

import com.seiko.net.download.util.recreate
import com.seiko.net.download.util.shadow
import com.seiko.net.util.throwIfFatal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import okio.*
import java.io.File

class NormalFlowDownloader : FlowDownloader {

  private var alreadyDownloaded = false

  private lateinit var file: File
  private lateinit var shadowFile: File

  override fun download(task: Task, response: Response): Flow<Progress> {
    file = task.getFile()
    shadowFile = file.shadow()

    beforeDownload(task, response)

    val totalSize = response.headersContentLength()

    return if (alreadyDownloaded) {
      flowOf(
        Progress(
          downloadSize = totalSize,
          totalSize = totalSize,
        )
      )
    } else {
      startDownload(
        response.throwIfFatal(), Progress(
          totalSize = totalSize,
        )
      )
    }
  }

  private fun beforeDownload(task: Task, response: Response) {
    val fileDir = task.getDir()
    if (!fileDir.exists() || !fileDir.isDirectory) {
      fileDir.mkdirs()
    }

    if (file.exists()) {
      if (file.length() == response.headersContentLength()) {
        alreadyDownloaded = true
        return
      }
      file.delete()
    }
    shadowFile.recreate()
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  private fun startDownload(body: ResponseBody, progress: Progress): Flow<Progress> {
    return flow {
      InternalState(
        body.source(),
        shadowFile.sink().buffer()
      ).apply {

        var readLean = source.read(buffer, 8192L)
        while (readLean != -1L) {
          sink.emit()
          emit(progress.apply {
            downloadSize += readLean
          })
          readLean = source.read(buffer, 8192L)
        }
        sink.flush()
        shadowFile.renameTo(file)

        sink.closeQuietly()
        source.closeQuietly()
      }
    }
  }

  private class InternalState(
    val source: BufferedSource,
    val sink: BufferedSink,
    val buffer: Buffer = sink.buffer
  )
}
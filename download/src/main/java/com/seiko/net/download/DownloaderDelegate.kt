package com.seiko.net.download

import com.seiko.net.download.util.recreate
import okhttp3.Response
import okhttp3.internal.headersContentLength
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import java.io.File

abstract class DownloaderDelegate {

  protected var alreadyDownloaded = false

  protected lateinit var file: File
  protected lateinit var shadowFile: File

  protected fun beforeDownload(task: Task, response: Response) {
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
}

class InternalState(
  val source: BufferedSource,
  val sink: BufferedSink,
  val buffer: Buffer = sink.buffer
)
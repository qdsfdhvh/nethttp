package com.seiko.net.download

import com.seiko.net.download.util.recreate
import com.seiko.net.download.util.shadow
import com.seiko.net.util.throwIfFatal
import io.reactivex.rxjava3.core.Emitter
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiConsumer
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Supplier
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import okio.*
import java.io.File

class NormalRxDownloader : RxDownloader {

  private var alreadyDownloaded = false

  private lateinit var file: File
  private lateinit var shadowFile: File

  override fun download(task: Task, response: Response): Flowable<Progress> {
    file = task.getFile()
    shadowFile = file.shadow()

    beforeDownload(task, response)

    val totalSize = response.headersContentLength()

    return if (alreadyDownloaded) {
      Flowable.just(
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

  private fun startDownload(body: ResponseBody, progress: Progress): Flowable<Progress> {
    return Flowable.generate(
      Supplier {
        InternalState(
          body.source(),
          shadowFile.sink().buffer()
        )
      },
      BiConsumer<InternalState, Emitter<Progress>> { internalState, emitter ->
        internalState.apply {
          val readLen = source.read(buffer, 8192L)
          if (readLen == -1L) {
            sink.flush()
            shadowFile.renameTo(file)
            emitter.onComplete()
          } else {
            sink.emit()
            emitter.onNext(progress.apply {
              downloadSize += readLen
            })
          }
        }
      },
      Consumer {
        it.apply {
          sink.closeQuietly()
          source.closeQuietly()
        }
      }
    )
  }

  private class InternalState(
    val source: BufferedSource,
    val sink: BufferedSink,
    val buffer: Buffer = sink.buffer
  )
}
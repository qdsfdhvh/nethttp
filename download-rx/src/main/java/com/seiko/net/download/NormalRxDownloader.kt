package com.seiko.net.download

import com.seiko.net.download.util.shadow
import com.seiko.net.throwIfFatal
import io.reactivex.rxjava3.core.Emitter
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiConsumer
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Supplier
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import okio.buffer
import okio.sink

class NormalRxDownloader : DownloaderDelegate(), RxDownloader {

  override fun download(taskInfo: TaskInfo, response: Response): Flowable<Progress> {
    val body = response.throwIfFatal()

    val task = taskInfo.task
    file = task.getFile()
    shadowFile = file.shadow()

    beforeDownload(task, response, taskInfo.isClearCache)

    val totalSize = response.headersContentLength()
    if (alreadyDownloaded) {
      return Flowable.just(Progress(totalSize, totalSize))
    }
    return startDownload(body, Progress(totalSize))
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
          val readLen = source.read(buffer, DEFAULT_BUFFER_SIZE)
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
          body.closeQuietly()
        }
      }
    )
  }
}
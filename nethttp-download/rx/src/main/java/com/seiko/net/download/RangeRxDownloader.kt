package com.seiko.net.download

import com.seiko.net.download.util.shadow
import com.seiko.net.download.util.tmp
import com.seiko.net.util.throwIfFatal
import io.reactivex.rxjava3.core.Emitter
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiConsumer
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Supplier
import okhttp3.Response
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import java.io.File

class RangeRxDownloader : DownloaderRangeDelegate(), RxDownloader {

  override fun download(taskInfo: TaskInfo, response: Response): Flowable<Progress> {
    file = taskInfo.task.getFile()
    shadowFile = file.shadow()
    tmpFile = file.tmp()

    beforeDownload(taskInfo, response)

    if (alreadyDownloaded) {
      val totalSize = response.headersContentLength()
      return Flowable.just(Progress(totalSize, totalSize))
    }
    return startDownload(taskInfo, response)
  }

  private fun startDownload(taskInfo: TaskInfo, response: Response): Flowable<Progress> {
    val progress = rangeTmpFile.lastProgress()

    val sources = rangeTmpFile.unCompleteSegments()
      .map { InnerRxDownloader(taskInfo, it, shadowFile, tmpFile).download() }

    return Flowable.mergeDelayError(sources, taskInfo.maxConCurrency)
      .map { progress.apply { downloadSize += it } }
      .doOnComplete {
        shadowFile.renameTo(file)
        tmpFile.delete()
        response.closeQuietly()
      }
  }

  private inner class InnerRxDownloader(
    private val taskInfo: TaskInfo,
    private val segment: Segment,
    private val shadowFile: File,
    private val tmpFile: File,
  ) {

    fun download(): Flowable<Long> {
      val headers = mapOf("Range" to "bytes=${segment.current}-${segment.end}")
      return get(taskInfo, headers)
        .flatMapPublisher { rangeSave(segment, it) }
    }

    private fun rangeSave(
      segment: Segment,
      response: Response
    ): Flowable<Long> {
      return Flowable.generate(
        Supplier {
          createInternalRangeState(
            shadowFile = shadowFile,
            tmpFile = tmpFile,
            segment = segment,
            body = response.throwIfFatal(),
          )
        },
        BiConsumer<InternalRangeState, Emitter<Long>> { internalState, emitter ->
          internalState.apply {
            val buffer = ByteArray(8192)
            val readLen = source.read(buffer)
            if (readLen == -1) {
              emitter.onComplete()
            } else {
              shadowFileBuffer.put(buffer, 0, readLen)
              current += readLen
              tmpFileBuffer.putLong(16, current)
              emitter.onNext(readLen.toLong())
            }
          }
        },
        Consumer {
          it.apply {
            tmpFileChannel.closeQuietly()
            shadowFileChannel.closeQuietly()
            source.closeQuietly()
            response.closeQuietly()
          }
        }
      )
    }
  }
}


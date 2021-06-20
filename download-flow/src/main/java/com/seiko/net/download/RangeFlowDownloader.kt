package com.seiko.net.download

import com.seiko.net.download.util.shadow
import com.seiko.net.download.util.tmp
import com.seiko.net.throwIfFatal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import java.io.File
import java.util.concurrent.Executors

class RangeFlowDownloader : DownloaderRangeDelegate(), FlowDownloader {

  override fun download(taskInfo: TaskInfo, response: Response): Flow<Progress> {
    file = taskInfo.task.getFile()
    shadowFile = file.shadow()
    tmpFile = file.tmp()

    beforeDownload(taskInfo, response, taskInfo.isClearCache)

    if (alreadyDownloaded) {
      val totalSize = response.headersContentLength()
      return flowOf(Progress(totalSize, totalSize))
    }
    return startDownload(taskInfo, response)
  }

  @OptIn(FlowPreview::class)
  private fun startDownload(taskInfo: TaskInfo, response: Response): Flow<Progress> {
    val progress = rangeTmpFile.lastProgress()

    val sources = rangeTmpFile.unCompleteSegments()
      .map { InnerFlowDownloader(taskInfo, it, shadowFile, tmpFile).download() }

    return sources
      .merge(taskInfo.maxConCurrency)
      .map { progress.apply { downloadSize += it } }
      .onCompletion {
        shadowFile.renameTo(file)
        tmpFile.delete()
        response.closeQuietly()
      }
  }


  private inner class InnerFlowDownloader(
    private val taskInfo: TaskInfo,
    private val segment: Segment,
    private val shadowFile: File,
    private val tmpFile: File,
  ) {

    @OptIn(FlowPreview::class)
    fun download(): Flow<Long> {
      val headers = mapOf("Range" to "bytes=${segment.current}-${segment.end}")
      return get(taskInfo, headers)
        .transform { emitAll(rangeSave(segment, it)) }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun rangeSave(
      segment: Segment,
      response: Response
    ): Flow<Long> {
      return flow {
        createInternalRangeState(
          shadowFile = shadowFile,
          tmpFile = tmpFile,
          segment = segment,
          body = response.throwIfFatal(),
        ).apply {

          val buffer = ByteArray(8192)
          var readLen = source.read(buffer)
          while (readLen != -1) {
            shadowFileBuffer.put(buffer, 0, readLen)
            current += readLen
            tmpFileBuffer.putLong(16, current)
            emit(readLen.toLong())
            readLen = source.read(buffer)
          }

          tmpFileChannel.closeQuietly()
          shadowFileChannel.closeQuietly()
          source.closeQuietly()
          response.closeQuietly()
        }
      }
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Iterable<Flow<T>>.merge(maxConCurrency: Int): Flow<T> {
  return channelFlow {
    val coroutineDispatcher = Executors.newFixedThreadPool(maxConCurrency)
      .asCoroutineDispatcher()
    forEach { flow ->
      launch(coroutineDispatcher) {
        flow.collect { send(it) }
      }
    }
    awaitClose {
      coroutineDispatcher.close()
    }
  }
}
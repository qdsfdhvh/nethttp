package com.seiko.net.download

import com.seiko.net.download.util.channel
import com.seiko.net.download.util.recreate
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.headersContentLength
import java.io.File
import java.io.InputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

abstract class DownloaderRangeDelegate {

  protected var alreadyDownloaded = false

  protected lateinit var file: File
  protected lateinit var shadowFile: File
  protected lateinit var tmpFile: File
  protected lateinit var rangeTmpFile: RangeTmpFile

  protected fun beforeDownload(taskInfo: TaskInfo, response: Response, isClearCache: Boolean) {
    val fileDir = taskInfo.task.getDir()
    if (!fileDir.exists() || !fileDir.isDirectory) {
      fileDir.mkdirs()
    }

    if (file.exists()) {
      if (!isClearCache && file.length() == response.headersContentLength()) {
        alreadyDownloaded = true
        return
      }
      file.delete()
    }
    if (!isClearCache && shadowFile.exists() && tmpFile.exists()) {
      rangeTmpFile = RangeTmpFile(tmpFile)
      if (rangeTmpFile.read(taskInfo, response)) {
        return
      }
    }
    createFiles(taskInfo, response)
  }

  private fun createFiles(taskInfo: TaskInfo, response: Response) {
    tmpFile.recreate {
      shadowFile.recreate(response.headersContentLength()) {
        rangeTmpFile = RangeTmpFile(tmpFile)
        rangeTmpFile.write(taskInfo, response)
      }
    }
  }
}

fun createInternalRangeState(
  shadowFile: File,
  tmpFile: File,
  segment: Segment,
  body: ResponseBody,
): InternalRangeState {
  val source = body.byteStream()
  val shadowFileChannel = shadowFile.channel()
  val tmpFileChannel = tmpFile.channel()
  val tmpFileBuffer = tmpFileChannel.map(
    FileChannel.MapMode.READ_WRITE,
    segment.startPosition(),
    SEGMENT_SIZE,
  )
  val shadowFileBuffer = shadowFileChannel.map(
    FileChannel.MapMode.READ_WRITE,
    segment.current,
    segment.remainSize()
  )
  return InternalRangeState(
    source = source,
    shadowFileChannel = shadowFileChannel,
    shadowFileBuffer = shadowFileBuffer,
    tmpFileChannel = tmpFileChannel,
    tmpFileBuffer = tmpFileBuffer,
  )
}

class InternalRangeState(
  val source: InputStream,
  val shadowFileChannel: FileChannel,
  val shadowFileBuffer: MappedByteBuffer,
  val tmpFileChannel: FileChannel,
  val tmpFileBuffer: MappedByteBuffer,
  var current: Long = 0,
)
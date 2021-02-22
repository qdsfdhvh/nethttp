package com.seiko.net.download

import com.seiko.net.download.util.sliceCount
import okhttp3.Response
import okhttp3.internal.headersContentLength
import okio.*
import okio.ByteString.Companion.decodeHex
import java.io.File

private const val FILE_HEADER_MAGIC_NUMBER = "a1b2c3d4e5f6"

//How to calc: ByteString.decodeHex(FILE_HEADER_MAGIC_NUMBER).size() = 6
private const val FILE_HEADER_MAGIC_NUMBER_SIZE = 6L

//total header size
private const val FILE_HEADER_SIZE = FILE_HEADER_MAGIC_NUMBER_SIZE + 16L

class RangeTmpFile(
  private val tmpFile: File
) {

  private val header = FileHeader()
  private val content = FileContent()

  fun write(taskInfo: TaskInfo, response: Response) {
    val totalSize = response.headersContentLength()
    val totalSegments = response.sliceCount(taskInfo.rangeSize)
    tmpFile.sink().buffer().use {
      header.write(it, totalSize, totalSegments)
      content.write(it, totalSize, totalSegments, taskInfo.rangeSize)
    }
  }

  fun read(taskInfo: TaskInfo, response: Response): Boolean {
    val totalSize = response.headersContentLength()
    val totalSegments = response.sliceCount(taskInfo.rangeSize)
    tmpFile.source().buffer().use {
      header.read(it)
      content.read(it, header.totalSegments)
    }
    return header.check(totalSize, totalSegments)
  }

  fun unCompleteSegments(): List<Segment> {
    return content.segments.filter { !it.isComplete() }
  }

  fun lastProgress(): Progress {
    return Progress(
      downloadSize = content.downloadSize(),
      totalSize = header.totalSize
    )
  }
}

private class FileHeader(
  var totalSize: Long = 0L,
  var totalSegments: Long = 0L,
) {
  fun write(sink: BufferedSink, totalSize: Long, totalSegments: Long) {
    this.totalSize = totalSize
    this.totalSegments = totalSegments
    sink.apply {
      write(FILE_HEADER_MAGIC_NUMBER.decodeHex())
      writeLong(totalSize)
      writeLong(totalSegments)
    }
  }

  fun read(source: BufferedSource) {
    val header = source.readByteString(FILE_HEADER_MAGIC_NUMBER_SIZE).hex()
    if (header != FILE_HEADER_MAGIC_NUMBER) {
      return
    }
    totalSize = source.readLong()
    totalSegments = source.readLong()
  }

  fun check(totalSize: Long, totalSegments: Long): Boolean {
    return this.totalSize == totalSize
      && this.totalSegments == totalSegments
  }
}

private class FileContent {

  var segments = mutableListOf<Segment>()

  fun write(
    sink: BufferedSink,
    totalSize: Long,
    totalSegments: Long,
    rangeSize: Long,
  ) {
    segments.clear()
    sliceSegments(totalSize, totalSegments, rangeSize)
    segments.forEach { it.write(sink) }
  }

  fun read(
    source: BufferedSource,
    totalSegments: Long
  ) {
    segments.clear()
    for (i in 0 until totalSegments) {
      segments.add(Segment().read(source))
    }
  }

  fun downloadSize(): Long {
    return segments.sumOf { it.completeSize() }
  }

  private fun sliceSegments(
    totalSize: Long,
    totalSegments: Long,
    rangeSize: Long
  ) {
    var start = 0L
    for (i in 0 until totalSegments) {
      val end = if (i == totalSegments - 1) {
        totalSize - 1
      } else {
        start + rangeSize - 1
      }
      segments.add(Segment(i, start, start, end))
      start += rangeSize
    }
  }
}

class Segment(
  var index: Long = 0L,
  var start: Long = 0L,
  var current: Long = 0L,
  var end: Long = 0L,
) {

  fun write(sink: BufferedSink) = apply {
    sink.apply {
      writeLong(index)
      writeLong(start)
      writeLong(current)
      writeLong(end)
    }
  }

  fun read(source: BufferedSource) = apply {
    val buffer = Buffer()
    source.readFully(buffer, SEGMENT_SIZE)
    buffer.apply {
      index = readLong()
      start = readLong()
      current = readLong()
      end = readLong()
    }
  }

  fun isComplete() = (current - end) == 1L

  fun remainSize() = end - current + 1L

  fun completeSize() = current - start

  fun startPosition() = FILE_HEADER_SIZE + SEGMENT_SIZE + index
}
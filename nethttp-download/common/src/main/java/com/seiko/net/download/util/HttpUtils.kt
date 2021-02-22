package com.seiko.net.download.util

import okhttp3.Response
import okhttp3.internal.headersContentLength

fun Response.isSupportRange(): Boolean {
  if (code == 206
    || header("Content-Range")?.isNotEmpty() == true
    || header("Accept-Ranges") == "bytes"
  ) return true
  return false
}

fun Response.sliceCount(rangeSize: Long): Long {
  val totalSize = headersContentLength()
  var result = totalSize / rangeSize
  if (totalSize % rangeSize != 0L) result++
  return result
}

fun Response.url(): String {
  return request.url.toString()
}
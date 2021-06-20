package com.seiko.net.internal.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

internal fun Uri.asRequestBody(
  context: Context,
  contentType: MediaType? = null
): RequestBody {
  return UriRequestBody(
    uri = this,
    contentType = contentType,
    contentResolver = context.contentResolver,
  )
}

private class UriRequestBody(
  private val uri: Uri,
  private val contentType: MediaType?,
  private val contentResolver: ContentResolver,
) : RequestBody() {

  override fun contentType(): MediaType? {
    val contentType = contentType
    if (contentType != null) {
      return contentType
    }
    if (uri.scheme == ContentResolver.SCHEME_FILE) {
      return uri.lastPathSegment?.getMediaType()
    }
    return contentResolver.getType(uri)?.toMediaType()
  }

  override fun contentLength(): Long {
    return contentResolver.openFileDescriptor(uri, "r")?.statSize ?: -1
  }

  override fun writeTo(sink: BufferedSink) {
    val source = contentResolver.openInputStream(uri)?.source() ?: return
    sink.writeAll(source)
  }
}
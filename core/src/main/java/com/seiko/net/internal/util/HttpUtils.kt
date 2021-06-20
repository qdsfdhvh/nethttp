package com.seiko.net.internal.util

import com.seiko.net.internal.KeyValue
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import java.net.URLConnection

internal fun HttpUrl.Builder.addQueryParameters(
  queryParams: Set<KeyValue>
): HttpUrl.Builder {
  queryParams.forEach { param ->
    if (param.encode) {
      addEncodedQueryParameter(param.key, param.value.toString())
    } else {
      addQueryParameter(param.key, param.value.toString())
    }
  }
  return this
}

internal fun FormBody.Builder.addParams(
  params: Set<KeyValue>
): FormBody.Builder {
  params.forEach { param ->
    if (param.encode) {
      addEncoded(param.key, param.value.toString())
    } else {
      add(param.key, param.value.toString())
    }
  }
  return this
}

internal fun MultipartBody.Builder.addParts(
  parts: Collection<MultipartBody.Part>
): MultipartBody.Builder {
  parts.forEach { part ->
    addPart(part)
  }
  return this
}

internal fun String.getMediaType(): MediaType {
  val fileSuffix = substringAfterLast('.')
  val contentType = URLConnection.guessContentTypeFromName(fileSuffix)
  return contentType.toMediaType()
}
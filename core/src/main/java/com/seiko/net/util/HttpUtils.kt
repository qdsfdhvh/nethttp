package com.seiko.net.util

import com.seiko.net.exception.HttpStatusCodeException
import com.seiko.net.model.KeyValue
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.net.URLConnection

fun Response.throwIfFatal(): ResponseBody {
  if (isSuccessful) return body!!
  throw HttpStatusCodeException(this)
}

fun HttpUrl.Builder.addQueryParameters(
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

fun FormBody.Builder.addParams(
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

fun MultipartBody.Builder.addParts(
  parts: Collection<MultipartBody.Part>
): MultipartBody.Builder {
  parts.forEach { part ->
    addPart(part)
  }
  return this
}

fun String.getMediaType(): MediaType {
  val fileSuffix = substringAfterLast('.')
  val contentType = URLConnection.guessContentTypeFromName(fileSuffix)
  return contentType.toMediaType()
}
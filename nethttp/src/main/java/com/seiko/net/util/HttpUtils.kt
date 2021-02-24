package com.seiko.net.util

import com.seiko.net.exception.HttpStatusCodeException
import com.seiko.net.model.KeyValue
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.ResponseBody

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
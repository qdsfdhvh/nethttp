package com.seiko.net.util

import com.seiko.net.exception.HttpStatusCodeException
import okhttp3.Response
import okhttp3.ResponseBody

fun Response.throwIfFatal(): ResponseBody {
  if (isSuccessful) return body!!
  throw HttpStatusCodeException(this)
}
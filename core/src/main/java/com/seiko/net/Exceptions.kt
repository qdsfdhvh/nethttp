package com.seiko.net

import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Response
import java.io.IOException

class HttpStatusCodeException(response: Response) : IOException(response.message)

class ResponseParseException(
  private val errorCode: String,
  errorMessage: String,
  response: Response
) : IOException(errorMessage) {

  private val requestMethod: String
  private val httpUrl: HttpUrl
  private val responseHeaders: Headers

  init {
    val request = response.request
    requestMethod = request.method
    httpUrl = request.url
    responseHeaders = response.headers
  }

  override fun getLocalizedMessage(): String = errorCode

  override fun toString(): String {
    return """${javaClass.name}:
      $requestMethod $httpUrl
      Code=$errorCode message=$message
      $responseHeaders"""
  }
}
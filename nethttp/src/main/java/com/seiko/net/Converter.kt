package com.seiko.net

import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

interface Converter {

  fun <T> convert(value: T, type: Type): RequestBody

  fun <T> convert(responseBody: ResponseBody, type: Type): T
}
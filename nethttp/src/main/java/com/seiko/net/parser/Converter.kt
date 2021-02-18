package com.seiko.net.parser

import androidx.annotation.WorkerThread
import com.seiko.net.NetHttp
import com.seiko.net.getActualTypeParameter
import com.seiko.net.throwIfFatal
import okhttp3.Response

open class ConverterParser<T>(private val netHttp: NetHttp.Call) : ExecuteParser<T> {

  override fun onParse(response: Response): T {
    val clazz = javaClass
    val type = getActualTypeParameter(clazz, 0)
    return netHttp.converter().convert(response.throwIfFatal(), type)
  }

  override fun execute(): T = netHttp.toParse(this)
}

inline fun <reified T> NetHttp.Call.asConvert(): ExecuteParser<T> {
  return object : ConverterParser<T>(this) {}
}

@WorkerThread
inline fun <reified T> NetHttp.Call.toConvert(): T {
  return asConvert<T>().execute()
}

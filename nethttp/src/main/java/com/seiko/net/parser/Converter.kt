package com.seiko.net.parser

import androidx.annotation.WorkerThread
import com.seiko.net.NetHttp
import com.seiko.net.util.getActualTypeParameter
import com.seiko.net.util.throwIfFatal
import okhttp3.Response

open class ConverterParser<T>(
  private val netHttp: NetHttp.Call
) : ExecuteParser<T> {

  override fun onParse(response: Response): T {
    val clazz = javaClass
    val type = getActualTypeParameter(clazz, 0)
    return netHttp.converter().convert(response.throwIfFatal(), type)
  }

  override fun execute(): T = netHttp.useParse(this)
}

inline fun <reified T> NetHttp.Call.asConvert(): ExecuteParser<T> =
  object : ConverterParser<T>(this) {}

@WorkerThread
inline fun <reified T> NetHttp.Call.toConvert(): T =
  asConvert<T>().execute()

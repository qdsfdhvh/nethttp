package com.seiko.net.parser

import androidx.annotation.WorkerThread
import com.seiko.net.NetHttp
import com.seiko.net.util.TypeLiteral
import com.seiko.net.util.throwIfFatal
import okhttp3.Response
import java.lang.reflect.Type

class ConverterParser<T>(
  private val netHttp: NetHttp.Call,
  private val type: Type,
) : ExecuteParser<T> {

  override fun onParse(response: Response): T {
    return netHttp.converter().convert(response.throwIfFatal(), type)
  }

  override fun execute(): T = netHttp.useParse(this)
}

fun <T> NetHttp.Call.asConvert(type: Type): ExecuteParser<T> =
  ConverterParser(this, type)

inline fun <reified T> NetHttp.Call.asConvert(): ExecuteParser<T> =
  asConvert(object : TypeLiteral<T>() {}.type)

fun <T> NetHttp.Call.toConvert(type: Type): T =
  useParse(object : Parser<T> {
    override fun onParse(response: Response): T {
      return converter().convert(response.throwIfFatal(), type)
    }
  })

@WorkerThread
inline fun <reified T> NetHttp.Call.toConvert(): T =
  toConvert(object : TypeLiteral<T>() {}.type)

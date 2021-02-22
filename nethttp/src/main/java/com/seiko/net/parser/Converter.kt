package com.seiko.net.parser

import androidx.annotation.WorkerThread
import com.seiko.net.NetHttp
import com.seiko.net.util.TypeLiteral
import com.seiko.net.util.convert
import okhttp3.Response
import java.lang.reflect.Type

fun <T> NetHttp.Call.asConvert(type: Type): ExecuteParser<T> =
  object : ExecuteParser<T> {
    override fun onParse(response: Response): T = converter().convert<T>(response, type)
    override fun execute(): T = useParse(this)
  }

inline fun <reified T> NetHttp.Call.asConvert(): ExecuteParser<T> =
  asConvert(object : TypeLiteral<T>() {}.type)

fun <T> NetHttp.Call.toConvert(type: Type): T =
  useParse(object : Parser<T> {
    override fun onParse(response: Response): T = converter().convert<T>(response, type)
  })

@WorkerThread
inline fun <reified T> NetHttp.Call.toConvert(): T =
  toConvert(object : TypeLiteral<T>() {}.type)

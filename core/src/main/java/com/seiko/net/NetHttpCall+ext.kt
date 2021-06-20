package com.seiko.net

import androidx.annotation.WorkerThread
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

@WorkerThread
fun <T> NetHttp.Call.execute(parser: Parser<T>): T {
  return parser.onParse(newCall().execute())
}

fun <T> NetHttp.Call.enqueue(parser: Parser<T>, callback: NetHttp.Callback<T>) {
  newCall().enqueue(object : okhttp3.Callback {
    override fun onResponse(call: okhttp3.Call, response: Response) {
      callback.onSuccess(parser.onParse(response))
    }

    override fun onFailure(call: okhttp3.Call, e: IOException) {
      callback.onFailure(e)
    }
  })
}

object StringParser : Parser<String> {
  override fun onParse(response: Response): String {
    return response.use { it.body?.string().orEmpty() }
  }
}

object ResponseParser : Parser<Response> {
  override fun onParse(response: Response): Response {
    return response
  }
}

fun <T> NetHttp.Call.convertParser(type: Type): Parser<T> = object : Parser<T> {
  override fun onParse(response: Response): T {
    return response.use { converter().convert<T>(it, type) }
  }
}

fun NetHttp.Call.asBodyString(callback: NetHttp.Callback<String>) {
  enqueue(StringParser, callback)
}

fun NetHttp.Call.asOkResponse(callback: NetHttp.Callback<Response>) {
  enqueue(ResponseParser, callback)
}

fun <T> NetHttp.Call.asConvert(type: Type, callback: NetHttp.Callback<T>) {
  enqueue(convertParser(type), callback)
}

inline fun <reified T> NetHttp.Call.asConvert(callback: NetHttp.Callback<T>) {
  asConvert(object : TypeLiteral<T>() {}.type, callback)
}

@WorkerThread
fun NetHttp.Call.toBodyString(): String = execute(StringParser)

@WorkerThread
fun NetHttp.Call.toOkResponse(): Response = execute(ResponseParser)

@WorkerThread
fun <T> NetHttp.Call.toConvert(type: Type): T = execute(convertParser(type))

@WorkerThread
inline fun <reified T> NetHttp.Call.toConvert(): T = toConvert(object : TypeLiteral<T>() {}.type)
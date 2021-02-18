package com.seiko.net.parser

import androidx.annotation.WorkerThread
import com.seiko.net.NetHttp
import okhttp3.Response

interface Parser<out T> {
  fun onParse(response: Response): T
}

interface ExecuteParser<out T> : Parser<T> {
  @WorkerThread
  fun execute(): T
}

@WorkerThread
fun <T> NetHttp.Call.toParse(parser: Parser<T>): T {
  return newCall().execute().use(parser::onParse)
}

internal fun <T> Parser<T>.asExecuteParser(netHttp: NetHttp.Call): ExecuteParser<T> {
  return object : ExecuteParser<T>, Parser<T> by this {
    override fun execute(): T = netHttp.toParse(this@asExecuteParser)
  }
}
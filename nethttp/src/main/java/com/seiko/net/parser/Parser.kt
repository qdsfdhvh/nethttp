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
fun <T> NetHttp.Call.useParse(parser: Parser<T>): T {
  return newCall().execute().use(parser::onParse)
}

@WorkerThread
fun <T> NetHttp.Call.letParse(parser: Parser<T>): T {
  return newCall().execute().let(parser::onParse)
}

internal fun <T> Parser<T>.useExecuteParser(netHttp: NetHttp.Call): ExecuteParser<T> {
  return object : ExecuteParser<T>, Parser<T> by this {
    override fun execute(): T = netHttp.useParse(this)
  }
}

internal fun <T> Parser<T>.letExecuteParser(netHttp: NetHttp.Call): ExecuteParser<T> {
  return object : ExecuteParser<T>, Parser<T> by this {
    override fun execute(): T = netHttp.letParse(this)
  }
}
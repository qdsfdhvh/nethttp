package com.seiko.net.parser

import androidx.annotation.WorkerThread
import com.seiko.net.NetHttp
import okhttp3.Response

private object OkResponseParser : Parser<Response> {
  override fun onParse(response: Response) = response
}

private object StringParser : Parser<String> {
  override fun onParse(response: Response) = response.body?.string().orEmpty()
}

fun NetHttp.Call.asOkResponse(): ExecuteParser<Response> =
  OkResponseParser.asExecuteParser(this)

fun NetHttp.Call.asBodyString(): ExecuteParser<String> =
  StringParser.asExecuteParser(this)

@WorkerThread
fun NetHttp.Call.toOkResponse(): Response = toParse(OkResponseParser)

@WorkerThread
fun NetHttp.Call.toBodyString(): String = toParse(StringParser)

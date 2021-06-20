package com.seiko.net

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

fun <T> NetHttp.Call.asFlow(parser: Parser<T>): Flow<T> = callbackFlow {
  enqueue(parser, object : NetHttp.Callback<T> {
    override fun onSuccess(data: T) {
      trySend(data)
    }

    override fun onFailure(e: IOException) {
      close(e)
    }
  })
  awaitClose()
}

fun NetHttp.Call.asFlowOkResponse(): Flow<Response> = asFlow(ResponseParser)

fun NetHttp.Call.asFlowString(): Flow<String> = asFlow(StringParser)

fun <T> NetHttp.Call.asFlow(type: Type): Flow<T> = asFlow(convertParser(type))

inline fun <reified T> NetHttp.Call.asFlow(): Flow<T> = asFlow(object : TypeLiteral<T>() {}.type)

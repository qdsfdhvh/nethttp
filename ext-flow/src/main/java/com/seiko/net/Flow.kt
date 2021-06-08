package com.seiko.net

import com.seiko.net.parser.toBodyString
import com.seiko.net.parser.toConvert
import com.seiko.net.parser.toOkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Response

fun NetHttp.Call.asFlowOkResponse(): Flow<Response> =
  flow { emit(toOkResponse()) }.flowOn(dispatcher())

fun NetHttp.Call.asFlowString(): Flow<String> =
  flow { emit(toBodyString()) }.flowOn(dispatcher())

fun <T> NetHttp.Call.asFlow(cls: Class<T>): Flow<T> =
  flow { emit(toConvert<T>(cls)) }.flowOn(dispatcher())

inline fun <reified T> NetHttp.Call.asFlow(): Flow<T> =
  flow { emit(toConvert<T>()) }.flowOn(dispatcher())

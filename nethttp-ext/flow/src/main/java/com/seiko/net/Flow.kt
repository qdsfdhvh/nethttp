package com.seiko.net

import com.seiko.net.parser.toBodyString
import com.seiko.net.parser.toConvert
import com.seiko.net.parser.toOkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Response

fun NetHttp.Call.asFlowOkResponse(
  dispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<Response> = flow { emit(toOkResponse()) }.flowOn(dispatcher)

fun NetHttp.Call.asFlowString(
  dispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<String> = flow { emit(toBodyString()) }.flowOn(dispatcher)

inline fun <reified T> NetHttp.Call.asFlow(
  dispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<T> = flow { emit(toConvert<T>()) }.flowOn(dispatcher)

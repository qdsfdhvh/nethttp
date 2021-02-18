package com.seiko.okhttp.flow.http

import com.seiko.net.NetHttp
import com.seiko.net.parser.toBodyString
import com.seiko.net.parser.toConvert
import com.seiko.net.parser.toOkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Response
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class)
fun <T> flowIO(@BuilderInference block: suspend FlowCollector<T>.() -> Unit) =
  flow(block).flowOn(Dispatchers.IO)

fun NetHttp.Call.asFlowOkResponse(): Flow<Response> = flowIO { emit(toOkResponse()) }

fun NetHttp.Call.asFlowString(): Flow<String> = flowIO { emit(toBodyString()) }

inline fun <reified T> NetHttp.Call.asFlow() = flowIO { emit(toConvert<T>()) }

package com.seiko.net

import com.seiko.net.parser.toBodyString
import com.seiko.net.parser.toConvert
import com.seiko.net.parser.toOkResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleFromCallable
import okhttp3.Response

fun NetHttp.Call.asSingleOkResponse(): Single<Response> =
  SingleFromCallable { toOkResponse() }.subscribeOn(scheduler())

fun NetHttp.Call.asSingleString(): Single<String> =
  SingleFromCallable { toBodyString() }.subscribeOn(scheduler())

inline fun <reified T> NetHttp.Call.asSingle(): Single<T> =
  SingleFromCallable { toConvert<T>() }.subscribeOn(scheduler())

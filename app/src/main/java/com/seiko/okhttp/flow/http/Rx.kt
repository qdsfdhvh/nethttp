package com.seiko.okhttp.flow.http

import com.seiko.net.NetHttp
import com.seiko.net.parser.toBodyString
import com.seiko.net.parser.toConvert
import com.seiko.net.parser.toOkResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Response

fun <T> createSingle(block: () -> T): Single<T> =
  SingleCreate.create<T> { emitter ->
    try {
      emitter.onSuccess(block())
    } catch (e: Exception) {
      emitter.onError(e)
    }
  }.subscribeOn(Schedulers.io())

fun NetHttp.Call.asSingleOkResponse(): Single<Response> = createSingle { toOkResponse() }

fun NetHttp.Call.asSingleString(): Single<String> = createSingle { toBodyString() }

inline fun <reified T> NetHttp.Call.asSingle() = createSingle { toConvert<T>() }

package com.seiko.net

import com.seiko.net.parser.toBodyString
import com.seiko.net.parser.toConvert
import com.seiko.net.parser.toOkResponse
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Response

fun <T> createSingle(block: () -> T): Single<T> =
  SingleCreate.create { emitter ->
    try {
      emitter.onSuccess(block())
    } catch (e: Exception) {
      emitter.onError(e)
    }
  }

fun NetHttp.Call.asSingleOkResponse(
  scheduler: Scheduler = Schedulers.io()
): Single<Response> = createSingle { toOkResponse() }.subscribeOn(scheduler)

fun NetHttp.Call.asSingleString(
  scheduler: Scheduler = Schedulers.io()
): Single<String> = createSingle { toBodyString() }.subscribeOn(scheduler)

inline fun <reified T> NetHttp.Call.asSingle(
  scheduler: Scheduler = Schedulers.io()
): Single<T> = createSingle { toConvert<T>() }.subscribeOn(scheduler)

package com.seiko.net

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

fun <T> NetHttp.Call.asSingle(parser: Parser<T>): Single<T> = SingleCreate { emitter ->
  enqueue(parser, object : NetHttp.Callback<T> {
    override fun onSuccess(data: T) {
      emitter.onSuccess(data)
    }

    override fun onFailure(e: IOException) {
      emitter.onError(e)
    }
  })
}

fun NetHttp.Call.asSingleOkResponse(): Single<Response> = asSingle(ResponseParser)

fun NetHttp.Call.asSingleString(): Single<String> = asSingle(StringParser)

fun <T> NetHttp.Call.asSingle(type: Type): Single<T> = asSingle(convertParser(type))

inline fun <reified T> NetHttp.Call.asSingle(): Single<T> = asSingle(object : TypeLiteral<T>() {}.type)

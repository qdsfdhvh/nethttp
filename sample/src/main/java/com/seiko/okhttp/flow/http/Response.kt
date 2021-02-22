package com.seiko.okhttp.flow.http

import com.seiko.net.NetHttp
import com.seiko.net.dispatcher
import com.seiko.net.exception.ParseException
import com.seiko.net.parser.Parser
import com.seiko.net.parser.useParse
import com.seiko.net.scheduler
import com.seiko.net.util.TypeLiteral
import com.seiko.net.util.throwIfFatal
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Types
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleFromCallable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.reflect.Type

@JsonClass(generateAdapter = true)
data class Response<T>(
  @field:Json(name = "errorCode") val code: Int = 0,
  @field:Json(name = "errorMsg") val msg: String = "",
  val data: T? = null
)

class ResponseParser<T>(
  private val netHttp: NetHttp.Call,
  private val type: Type,
) : Parser<T> {
  @Suppress("UNCHECKED_CAST")
  override fun onParse(response: okhttp3.Response): T {
    val responseType = Types.newParameterizedType(Response::class.java, type)
    val result = netHttp.converter().convert<Response<T>>(response.throwIfFatal(), responseType)
    if (result.code == 0) {
      val data = result.data
      if (data != null) return data
      if (type == String::class.java) return "" as T
      if (type == Boolean::class.javaObjectType) return true as T
    }
    throw ParseException(result.code.toString(), result.msg, response)
  }
}

inline fun <reified T : Any> NetHttp.Call.toResponse(): T =
  useParse(ResponseParser(this, object : TypeLiteral<T>() {}.type))

inline fun <reified T : Any> NetHttp.Call.asFlowResponse(): Flow<T> =
  flow { emit(toResponse<T>()) }.flowOn(dispatcher())

inline fun <reified T : Any> NetHttp.Call.asSingleResponse(): Single<T> =
  SingleFromCallable { toResponse<T>() }.subscribeOn(scheduler())
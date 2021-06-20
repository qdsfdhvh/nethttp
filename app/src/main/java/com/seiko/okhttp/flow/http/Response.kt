package com.seiko.okhttp.flow.http

import com.seiko.net.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Types
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
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
    val result = netHttp.converter().convert<Response<T>>(response, responseType)
    if (result.code == 0) {
      val data = result.data
      if (data != null) return data
      if (type == String::class.java) return "" as T
      if (type == Boolean::class.javaObjectType) return true as T
    }
    throw ResponseParseException(result.code.toString(), result.msg, response)
  }
}

fun <T : Any> NetHttp.Call.toResponse(type: Type): T = execute(ResponseParser(this, type))

inline fun <reified T : Any> NetHttp.Call.toResponse(): T = toResponse(object : TypeLiteral<T>() {}.type)

fun <T : Any> NetHttp.Call.asResponse(type: Type, callback: NetHttp.Callback<T>) = enqueue(ResponseParser(this, type), callback)

inline fun <reified T : Any> NetHttp.Call.asResponse(callback: NetHttp.Callback<T>) = asResponse(object : TypeLiteral<T>() {}.type, callback)

fun <T : Any> NetHttp.Call.asFlowResponse(type: Type): Flow<T> = asFlow(ResponseParser(this, type))

inline fun <reified T : Any> NetHttp.Call.asFlowResponse(): Flow<T> = asFlowResponse(object : TypeLiteral<T>() {}.type)

fun <T : Any> NetHttp.Call.asSingleResponse(type: Type): Single<T> = asSingle(ResponseParser(this, type))

inline fun <reified T : Any> NetHttp.Call.asSingleResponse(): Single<T> = asSingleResponse(object : TypeLiteral<T>() {}.type)
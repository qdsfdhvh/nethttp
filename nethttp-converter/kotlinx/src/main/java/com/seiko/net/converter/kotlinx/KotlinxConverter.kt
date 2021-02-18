package com.seiko.net.converter.kotlinx

import com.seiko.net.Converter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type


@OptIn(ExperimentalSerializationApi::class)
class KotlinxConverter private constructor(json: Json) : Converter {

  private val serializer: Serializer = Serializer.FromString(json)

  override fun <T> convert(value: T, type: Type): RequestBody {
    val saver = serializer.serializer(type)
    return serializer.toRequestBody(MEDIA_TYPE, saver, value as Any)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> convert(responseBody: ResponseBody, type: Type): T {
    val loader = serializer.serializer(type)
    return serializer.fromResponseBody(loader, responseBody) as T
  }

  companion object {

    @JvmStatic
    fun create() = create(Json.Default)

    @JvmStatic
    fun create(json: Json) = KotlinxConverter(json)

    private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
  }
}

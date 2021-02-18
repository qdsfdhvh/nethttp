package com.seiko.net.converter.moshi

import com.seiko.net.Converter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import java.lang.reflect.Type

class MoshiConverter private constructor(
  private val moshi: Moshi,
  private val lenient: Boolean,
  private val failOnUnknown: Boolean,
  private val serializeNulls: Boolean,
) : Converter {

  override fun <T> convert(value: T, type: Type): RequestBody {
    var adapter = moshi.adapter<T>(wrapperType(type))
    if (lenient) {
      adapter = adapter.lenient()
    }
    if (failOnUnknown) {
      adapter = adapter.failOnUnknown()
    }
    if (serializeNulls) {
      adapter = adapter.serializeNulls()
    }
    val buffer = Buffer()
    val writer = JsonWriter.of(buffer)
    adapter.toJson(writer, value)
    return buffer.readByteString().toRequestBody(MEDIA_TYPE)
  }

  override fun <T> convert(responseBody: ResponseBody, type: Type): T {
    var adapter = moshi.adapter<T>(type)
    if (lenient) {
      adapter = adapter.lenient()
    }
    if (failOnUnknown) {
      adapter = adapter.failOnUnknown()
    }
    if (serializeNulls) {
      adapter = adapter.serializeNulls()
    }
    return responseBody.source().use { source ->
      if (source.rangeEquals(0, UTF8_BOM)) {
        source.skip(UTF8_BOM.size.toLong())
      }
      val reader = JsonReader.of(source)
      val result = adapter.fromJson(reader)
      if (reader.peek() != JsonReader.Token.END_DOCUMENT) {
        throw RuntimeException("JSON document was not fully consumed.")
      }
      result!!
    }
  }

  private fun wrapperType(type: Type): Type {
    return when(type) {
      HashMap::class.java -> Map::class.java
      LinkedHashMap::class.java -> Map::class.java
      else -> type
    }
  }

  companion object {

    @JvmStatic
    fun create() = create(Moshi.Builder().build())

    @JvmOverloads
    @JvmStatic
    fun create(
      moshi: Moshi,
      lenient: Boolean = false,
      failOnUnknown: Boolean = false,
      serializeNulls: Boolean = false,
    ) = MoshiConverter(
      moshi,
      lenient = lenient,
      failOnUnknown = failOnUnknown,
      serializeNulls = serializeNulls
    )

    private val UTF8_BOM: ByteString = "EFBBBF".decodeHex()
    private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()
  }
}
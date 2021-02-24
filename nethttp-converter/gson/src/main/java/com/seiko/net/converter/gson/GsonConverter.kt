package com.seiko.net.converter.gson

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import com.seiko.net.Converter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class GsonConverter private constructor(
  private val gson: Gson
) : Converter {

  override fun <T> convert(value: T, type: Type): RequestBody {
    val adapter = gson.getAdapter(TypeToken.get(type)) as TypeAdapter<T>
    val buffer = Buffer()
    val writer = buffer.outputStream().writer(Charsets.UTF_8)
    val jsonWriter = gson.newJsonWriter(writer)
    adapter.write(jsonWriter, value)
    jsonWriter.close()
    return buffer.readByteString().toRequestBody(MEDIA_TYPE)
  }

  override fun <T> convert(responseBody: ResponseBody, type: Type): T {
    val adapter = gson.getAdapter(TypeToken.get(type)) as TypeAdapter<T>
    val jsonReader = gson.newJsonReader(responseBody.charStream())
    val result = adapter.read(jsonReader)
    if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
      throw JsonIOException("JSON document was not fully consumed.")
    }
    return result
  }

  companion object {

    @JvmStatic
    fun create() = create(Gson())

    @JvmStatic
    fun create(gson: Gson) = GsonConverter(gson)

    private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
  }
}

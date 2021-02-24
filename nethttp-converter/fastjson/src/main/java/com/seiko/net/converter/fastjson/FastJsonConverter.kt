package com.seiko.net.converter.fastjson

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.parser.ParserConfig
import com.alibaba.fastjson.serializer.SerializeConfig
import com.seiko.net.Converter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

class FastJsonConverter private constructor(
  private val parserConfig: ParserConfig,
  private val serializeConfig: SerializeConfig,
) : Converter {

  override fun <T> convert(value: T, type: Type): RequestBody {
    return JSON.toJSONBytes(value, serializeConfig).toRequestBody(MEDIA_TYPE)
  }

  override fun <T> convert(responseBody: ResponseBody, type: Type): T {
    return JSON.parseObject(responseBody.byteStream(), Charsets.UTF_8, type, parserConfig)
  }

  companion object {

    @JvmOverloads
    @JvmStatic
    fun create(
      parserConfig: ParserConfig = ParserConfig.getGlobalInstance(),
      serializeConfig: SerializeConfig = SerializeConfig.getGlobalInstance(),
    ) = FastJsonConverter(parserConfig, serializeConfig)

    private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
  }
}
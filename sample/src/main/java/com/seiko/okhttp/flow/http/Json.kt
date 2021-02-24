package com.seiko.okhttp.flow.http

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.seiko.net.param.PostJsonParamNetHttp
import com.seiko.okhttp.flow.util.toAny
import com.seiko.okhttp.flow.util.toMap

fun PostJsonParamNetHttp.addJsonObject(jsonObject: String): PostJsonParamNetHttp {
  val element = JsonParser.parseString(jsonObject).asJsonObject
  return addJsonObject(element)
}

fun PostJsonParamNetHttp.addJsonObject(jsonObject: JsonObject): PostJsonParamNetHttp {
  addAll(jsonObject.toMap())
  return this
}

fun PostJsonParamNetHttp.addJsonElement(key: String, jsonElement: String): PostJsonParamNetHttp {
  val element = JsonParser.parseString(jsonElement)
  element.toAny()?.let { add(key, it) }
  return this
}
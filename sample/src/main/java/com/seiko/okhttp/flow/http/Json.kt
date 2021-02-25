package com.seiko.okhttp.flow.http

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.seiko.net.param.PostJsonParamNetHttp
import com.seiko.net.param.PostJsonRequestParams
import com.seiko.okhttp.flow.util.toAny
import com.seiko.okhttp.flow.util.toMap

fun PostJsonParamNetHttp.addJsonObject(jsonObject: JsonObject): PostJsonParamNetHttp {
  addAll(jsonObject.toMap())
  return this
}

fun PostJsonParamNetHttp.addJsonObject(jsonObject: String): PostJsonParamNetHttp {
  val element = JsonParser.parseString(jsonObject).asJsonObject
  return addJsonObject(element)
}

fun PostJsonParamNetHttp.addJsonElement(key: String, jsonElement: String): PostJsonParamNetHttp {
  val element = JsonParser.parseString(jsonElement)
  element.toAny()?.let { add(key, it) }
  return this
}

fun PostJsonRequestParams.addJsonObject(jsonObject: JsonObject) {
  addAll(jsonObject.toMap())
}

fun PostJsonRequestParams.addJsonObject(jsonObject: String) {
  addJsonObject(JsonParser.parseString(jsonObject).asJsonObject)
}

fun PostJsonRequestParams.addJsonElement(key: String, jsonElement: String) {
  JsonParser.parseString(jsonElement)?.toAny()?.let { add(key, it) }
}
package com.seiko.okhttp.flow.http

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.seiko.net.param.PostJsonRequestParams
import com.seiko.okhttp.flow.util.toAny
import com.seiko.okhttp.flow.util.toMap

fun PostJsonRequestParams.addJsonObject(jsonObject: JsonObject) {
  addAll(jsonObject.toMap())
}

fun PostJsonRequestParams.addJsonObject(jsonObject: String) {
  addJsonObject(JsonParser.parseString(jsonObject).asJsonObject)
}

fun PostJsonRequestParams.addJsonElement(key: String, jsonElement: String) {
  JsonParser.parseString(jsonElement)?.toAny()?.let { add(key, it) }
}
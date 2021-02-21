package com.seiko.okhttp.flow

// import kotlinx.serialization.json.*
//
// fun Map<*, *>.toJsonObject(): JsonObject {
//   val map = mutableMapOf<String, JsonElement>()
//   this.forEach {
//     if (it.key is String) {
//       map[it.key as String] = it.value.toJsonElement()
//     }
//   }
//   return JsonObject(map)
// }
//
// private fun Array<*>.toJsonArray(): JsonArray {
//   val array = mutableListOf<JsonElement>()
//   this.forEach { array.add(it.toJsonElement()) }
//   return JsonArray(array)
// }
//
// private fun List<*>.toJsonArray(): JsonArray {
//   val array = mutableListOf<JsonElement>()
//   this.forEach { array.add(it.toJsonElement()) }
//   return JsonArray(array)
// }
//
// private fun Any?.toJsonElement(): JsonElement {
//   return when (this) {
//     is Number -> JsonPrimitive(this)
//     is Boolean -> JsonPrimitive(this)
//     is String -> JsonPrimitive(this)
//     is Array<*> -> this.toJsonArray()
//     is List<*> -> this.toJsonArray()
//     is Map<*, *> -> this.toJsonObject()
//     is JsonElement -> this
//     else -> JsonNull
//   }
// }
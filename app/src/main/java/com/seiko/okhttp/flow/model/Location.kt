package com.seiko.okhttp.flow.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
  val longitude: Double,
  val latitude: Double,
)
package com.seiko.okhttp.flow.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TestData(
  val a: Int,
  val b: String,
)
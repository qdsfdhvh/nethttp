package com.seiko.okhttp.flow.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TagResponse(
  val name: String,
  val url: String
)
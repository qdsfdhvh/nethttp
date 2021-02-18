package com.seiko.okhttp.flow.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BannerResponse(
  val desc: String,
  val id: Int,
  val imagePath: String,
  val isVisible: Int,
  val order: Int,
  val title: String,
  val type: Int,
  val url: String,
)
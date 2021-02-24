package com.seiko.okhttp.flow.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Page<T>(
  val curPage: Int = 0,
  val datas: List<T> = emptyList(),
  val offset: Int = 0,
  val over: Boolean = false,
  val pageCount: Int = 0,
  val size: Int = 0,
  val total: Int = 0,
)

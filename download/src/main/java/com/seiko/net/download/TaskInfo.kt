package com.seiko.net.download

import com.seiko.net.NetHttp

class TaskInfo(
  val task: Task,
  val maxConCurrency: Int,
  val rangeSize: Long,
  val isClearCache: Boolean,
  val netHttp: NetHttp,
)
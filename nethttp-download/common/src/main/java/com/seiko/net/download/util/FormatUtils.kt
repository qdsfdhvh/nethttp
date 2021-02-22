package com.seiko.net.download.util

import java.math.RoundingMode

fun Long.formatSize(): String {
  if (this <= 0L) return ""

  val byte = this.toDouble()
  val kb = byte / 1024.0
  val mb = byte / 1024.0 / 1024.0
  val gb = byte / 1024.0 / 1024.0 / 1024.0
  val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0

  return when {
    tb >= 1 -> "${tb.decimal(2)} TB"
    gb >= 1 -> "${gb.decimal(2)} GB"
    mb >= 1 -> "${mb.decimal(2)} MB"
    kb >= 1 -> "${kb.decimal(2)} KB"
    else -> "${byte.decimal(2)} B"
  }
}

internal fun Double.decimal(digits: Int): Double {
  return this.toBigDecimal()
    .setScale(digits, RoundingMode.HALF_UP)
    .toDouble()
}

internal infix fun Long.ratio(bottom: Long): Double {
  if (bottom <= 0) {
    return 0.0
  }
  val result = (this * 100.0).toBigDecimal()
    .divide((bottom * 1.0).toBigDecimal(), 2, RoundingMode.HALF_UP)
  return result.toDouble()
}
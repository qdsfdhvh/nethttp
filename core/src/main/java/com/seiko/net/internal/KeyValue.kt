package com.seiko.net.internal

internal data class KeyValue(
  val key: String,
  val value: Any,
  val encode: Boolean = false,
) {

  override fun hashCode(): Int {
    return key.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as KeyValue
    if (key != other.key) return false
    if (value != other.value) return false
    if (encode != other.encode) return false
    return true
  }
}
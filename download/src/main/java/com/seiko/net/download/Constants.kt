package com.seiko.net.download

val RANGE_CHECK_HEADER = mapOf("Range" to "bytes=0-")

const val DEFAULT_BUFFER_SIZE = 8192L

const val DEFAULT_RANGE_SIZE = 5L * 1024 * 1024 //5M

const val DEFAULT_MAX_CONCURRENCY = 3

const val SEGMENT_SIZE = 32L // 4 * 8(long size)

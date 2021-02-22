package com.seiko.net.download

import com.seiko.net.download.util.formatSize
import com.seiko.net.download.util.ratio

class Progress(
  val totalSize: Long = 0,
  var downloadSize: Long = 0,
) {

  fun totalSizeStr(): String = totalSize.formatSize()

  fun downloadSizeStr(): String = downloadSize.formatSize()

  fun percent(): Double = downloadSize ratio totalSize

  fun percentStr(): String = "${percent()}%"
}
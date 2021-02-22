package com.seiko.net.download

import com.seiko.net.download.util.formatSize
import com.seiko.net.download.util.ratio

class Progress(
  val totalSize: Long = 0,
  var downloadSize: Long = 0,
) {
  val totalSizeFormat get() = totalSize.formatSize()
  val downloadSizeFormat get() = downloadSize.formatSize()
  val percent get() = downloadSize ratio totalSize
  val percentFormat get() = "${percent}%"
}
package com.seiko.net.download

import com.seiko.net.download.util.getFileNameFromUrl
import java.io.File

open class Task(
  var url: String,
  var savePath: String,
  var saveName: String,
) {

  open fun tag() = url

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (this === other) return true

    return if (other is Task) {
      tag() == other.tag()
    } else {
      false
    }
  }

  override fun hashCode(): Int {
    return tag().hashCode()
  }

  fun getDir(): File {
    return File(savePath)
  }

  fun getFile(): File {
    val saveName = if (saveName.isNotEmpty()) saveName else getFileNameFromUrl(url)
    return File(savePath, saveName)
  }
}
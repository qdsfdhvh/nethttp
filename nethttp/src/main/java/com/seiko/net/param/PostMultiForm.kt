package com.seiko.net.param

import android.content.Context
import android.net.Uri
import com.seiko.net.NetHttp
import com.seiko.net.util.addParts
import com.seiko.net.util.asRequestBody
import com.seiko.net.util.getMediaType
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun NetHttp.postMultiForm(url: String) = PostMultiFormParamNetHttp(this, url)

class PostMultiFormParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
) : AbsHeaderParamNetHttp<PostFormParamNetHttp>(netHttp) {

  private var mediaType: MediaType? = null
  private val parts = mutableListOf<Part>()

  fun setMultiForm() = apply { mediaType = MultipartBody.FORM }
  fun setMultiMixed() = apply { mediaType = MultipartBody.MIXED }
  fun setMultiAlternative() = apply { mediaType = MultipartBody.ALTERNATIVE }
  fun setMultiDigest() = apply { mediaType = MultipartBody.DIGEST }
  fun setMultiParallel() = apply { mediaType = MultipartBody.PARALLEL }

  fun addUri(context: Context, uri: Uri, contentType: MediaType? = null) = run {
    addPart(uri.asRequestBody(context, contentType))
  }

  fun addUri(
    context: Context,
    key: String,
    uri: Uri,
    fileName: String? = null,
    contentType: MediaType? = null,
  ) = run {
    val body = uri.asRequestBody(context, contentType)
    val part = Part.createFormData(key, fileName, body)
    addPart(part)
  }

  fun addFile(key: String, filePath: String) =
    addFile(key, File(filePath))

  fun addFile(key: String, filePath: String, fileName: String) =
    addFile(key, File(filePath), fileName)

  @JvmOverloads
  fun addFile(key: String, file: File, fileName: String = file.name) = run {
    val body = file.asRequestBody(fileName.getMediaType())
    addPart(Part.createFormData(key, fileName, body))
  }

  fun addFiles(key: String, fileList: List<File>) = apply {
    fileList.forEach { addFile(key, it) }
  }

  fun addFiles(fileMap: Map<String, File>) = apply {
    fileMap.forEach { addFile(it.key, it.value) }
  }

  fun addFilePaths(key: String, fileList: List<String>) = apply {
    fileList.forEach { addFile(key, it) }
  }

  fun addFilePaths(fileMap: Map<String, String>) = apply {
    fileMap.forEach { addFile(it.key, it.value) }
  }

  fun addPart(contentType: MediaType, content: ByteArray) =
    addPart(content.toRequestBody(contentType))

  fun addPart(contentType: MediaType, content: ByteArray, offset: Int, byteCount: Int) =
    addPart(content.toRequestBody(contentType, offset, byteCount))

  @JvmOverloads
  fun addPart(requestBody: RequestBody, headers: Headers? = null) =
    addPart(Part.create(headers, requestBody))

  fun addFormDataPart(name: String, value: String) =
    addPart(Part.createFormData(name, value))

  fun addFormDataPart(name: String, fileName: String, body: RequestBody) =
    addPart(Part.createFormData(name, fileName, body))

  fun addPart(part: Part) = apply {
    parts.add(part)
  }

  override fun buildRequest(): Request {
    val httpUrl = wrapperUrl(url).toHttpUrl()
    val bodyBuilder = MultipartBody.Builder()
    if (parts.isNotEmpty()) {
      bodyBuilder.addParts(parts)
      if (mediaType == null) {
        mediaType = MultipartBody.FORM
      }
    }
    val mediaType = mediaType
    if (mediaType != null) {
      bodyBuilder.setType(mediaType)
    }
    return Request.Builder()
      .url(httpUrl)
      .post(bodyBuilder.build())
      .headers(buildHeaders())
      .build()
  }
}
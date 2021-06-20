package com.seiko.net.param

import android.content.Context
import android.net.Uri
import com.seiko.net.NetHttp
import com.seiko.net.internal.util.addParts
import com.seiko.net.internal.util.asRequestBody
import com.seiko.net.internal.util.getMediaType
import okhttp3.*
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun NetHttp.postMultiForm(
  url: String,
  paramsBuilder: PostMultiFormRequestParams.() -> Unit = {}
) = PostMultiFormParamNetHttp(this, url, paramsBuilder)

class PostMultiFormParamNetHttp internal constructor(
  netHttp: NetHttp,
  private val url: String,
  private val paramsBuilder: PostMultiFormRequestParams.() -> Unit,
) : AbsHeaderParamNetHttp(netHttp) {

  private val params = PostMultiFormRequestParams()

  override fun requestParams(): HeaderRequestParams = params

  override fun buildRequest(): Request {
    params.run(paramsBuilder)
    return Request.Builder()
      .url(buildHttpUrl(url))
      .post(params.buildBody())
      .headers(buildHeaders())
      .build()
  }
}

class PostMultiFormRequestParams : HeaderRequestParams() {

  private var mediaType: MediaType? = null
  private val parts = mutableListOf<Part>()

  fun setMultiForm() {
    mediaType = MultipartBody.FORM
  }

  fun setMultiMixed() {
    mediaType = MultipartBody.MIXED
  }

  fun setMultiAlternative() {
    mediaType = MultipartBody.ALTERNATIVE
  }

  fun setMultiDigest() {
    mediaType = MultipartBody.DIGEST
  }

  fun setMultiParallel() {
    mediaType = MultipartBody.PARALLEL
  }

  fun addPart(part: Part) {
    parts.add(part)
  }

  fun addPart(requestBody: RequestBody, headers: Headers? = null) {
    addPart(Part.create(headers, requestBody))
  }

  fun addPart(contentType: MediaType, content: ByteArray) {
    addPart(content.toRequestBody(contentType))
  }

  fun addPart(contentType: MediaType, content: ByteArray, offset: Int, byteCount: Int) {
    addPart(content.toRequestBody(contentType, offset, byteCount))
  }

  fun addFormDataPart(name: String, value: String) {
    addPart(Part.createFormData(name, value))
  }

  fun addFormDataPart(name: String, fileName: String, body: RequestBody) {
    addPart(Part.createFormData(name, fileName, body))
  }

  fun addFile(key: String, filePath: String, fileName: String? = null) {
    addFile(key, File(filePath), fileName)
  }

  fun addFile(key: String, file: File, fileName: String? = null) {
    val name = fileName ?: file.name
    val body = file.asRequestBody(name.getMediaType())
    addPart(Part.createFormData(key, name, body))
  }

  fun addFiles(key: String, fileList: List<File>) {
    fileList.forEach { addFile(key, it) }
  }

  fun addFiles(fileMap: Map<String, File>) {
    fileMap.forEach { addFile(it.key, it.value) }
  }

  fun addFilePaths(key: String, fileList: List<String>) {
    fileList.forEach { addFile(key, it) }
  }

  fun addFilePaths(fileMap: Map<String, String>) {
    fileMap.forEach { addFile(it.key, it.value) }
  }

  fun addUri(context: Context, uri: Uri, contentType: MediaType? = null) {
    addPart(uri.asRequestBody(context, contentType))
  }

  fun addUri(
    context: Context,
    key: String,
    uri: Uri,
    fileName: String? = null,
    contentType: MediaType? = null,
  ) {
    val body = uri.asRequestBody(context, contentType)
    val part = Part.createFormData(key, fileName, body)
    addPart(part)
  }

  internal fun buildBody(): RequestBody {
    val builder = MultipartBody.Builder()
    if (parts.isNotEmpty()) {
      builder.addParts(parts)
      if (mediaType == null) {
        mediaType = MultipartBody.FORM
      }
    }
    val mediaType = mediaType
    if (mediaType != null) {
      builder.setType(mediaType)
    }
    return builder.build()
  }
}

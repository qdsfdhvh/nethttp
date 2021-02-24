package com.seiko.okhttp.flow.vm

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.seiko.net.asFlowString
import com.seiko.net.param.postMultiForm
import com.seiko.okhttp.flow.http.DownloadNetHttp
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class UploadViewModel : BaseRxViewModel() {

  val showMsg = mutableStateOf("")
  val toastState = mutableStateOf(false)

  fun upload(context: Context, uri: Uri) {
    DownloadNetHttp.postMultiForm(UPLOAD_URL)
      .addUri(context, "uploaded_file", uri)
      .asFlowString()
      .catch {
        Timber.w(it)
        showToast(it.message ?: it.toString())
      }
      .onEach { showToast("上传成功") }
      .launchIn(viewModelScope)
  }

  fun showToast(msg: String) {
    showMsg.value = msg
    toastState.value = true
  }

  companion object {
    private const val UPLOAD_URL = "http://t.xinhuo.com/index.php/Api/Pic/uploadPic"
  }
}
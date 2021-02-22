package com.seiko.okhttp.flow.ui

import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.net.NetHttp
import com.seiko.net.asFlow
import com.seiko.net.asSingle
import com.seiko.net.download.downloadFlow
import com.seiko.net.download.downloadRx
import com.seiko.net.param.get
import com.seiko.net.param.post
import com.seiko.net.parser.toConvert
import com.seiko.okhttp.flow.http.DownloadNetHttp
import com.seiko.okhttp.flow.http.GsonNetHttp
import com.seiko.okhttp.flow.http.Response
import com.seiko.okhttp.flow.http.asFlowResponse
import com.seiko.okhttp.flow.model.TestData
import com.seiko.okhttp.flow.model.response.BannerResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel : ViewModel() {

  private val _body = MutableLiveData<String>()
  val body: LiveData<String> = _body

  private val compositeDisposable = CompositeDisposable()

  fun clickFlow() {
    NetHttp.get("banner/json")
      .add("a", "aaa")
      .asFlow<Response<List<BannerResponse>>>()
      .catch { Timber.w(it) }
      .onEach { showBody(it.data?.get(0).toString()) }
      .launchIn(viewModelScope)
  }

  fun clickRx() {
    GsonNetHttp.post("banner/json")
      .add("a", "aaa")
      .add("b", TestData(1, "20"))
      .add("c", listOf("1", "2", "3"))
      .asSingle<Response<List<BannerResponse>>>()
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        showBody(it.data?.get(1).toString())
      }, { Timber.w(it) })
      .addToDisposables()
  }

  fun clickResponse() {
    NetHttp.post("banner/json")
      .body(TestData(11, "22"))
      .asFlowResponse<List<BannerResponse>>()
      .catch { Timber.w(it) }
      .onEach { showBody(it[2].toString()) }
      .launchIn(viewModelScope)
  }

  fun clickParse() {
    viewModelScope.launch {
      runCatching {
        withContext(Dispatchers.IO) {
          NetHttp.post("banner/json")
            .add("a", "aaa")
            .add("b", TestData(1, "20"))
            .add("c", listOf("1", "2", "3"))
            .toConvert<Response<List<BannerResponse>>>()
        }
      }.onSuccess {
        showBody(it.data?.get(3).toString())
      }.onFailure {
        Timber.w(it)
      }
    }
  }

  fun clickDownloadRx(context: Context) {
    DownloadNetHttp
      .downloadRx(
        url = "https://dldir1.qq.com/weixin/android/weixin706android1460.apk",
        savePath = context.defaultDownloadDir(),
        saveName = "微信",
      )
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        showBody("${it.downloadSizeStr()}/${it.totalSizeStr()}")
      }, {
        Timber.w(it)
      })
      .addToDisposables()
  }

  fun clickDownloadFlow(context: Context) {
    DownloadNetHttp
      .downloadFlow(
        url = "https://dldir1.qq.com/weixin/android/weixin706android1460.apk",
        savePath = context.defaultDownloadDir(),
        saveName = "微信-Flow",
      )
      .catch { Timber.w(it) }
      .onEach {
        showBody("${it.downloadSizeStr()}/${it.totalSizeStr()}")
      }
      .launchIn(viewModelScope)
  }

  private fun Disposable.addToDisposables() {
    compositeDisposable.add(this)
  }

  private fun showBody(body: String) {
    _body.value = body
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.dispose()
  }
}

private fun Context.defaultDownloadDir(): String {
  return getExternalFilesDir(DIRECTORY_DOWNLOADS)!!.absolutePath
}
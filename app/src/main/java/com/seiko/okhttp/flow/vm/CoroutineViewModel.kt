package com.seiko.okhttp.flow.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.net.*
import com.seiko.net.param.get
import com.seiko.net.param.postForm
import com.seiko.net.param.postJson
import com.seiko.okhttp.flow.Global
import com.seiko.okhttp.flow.http.*
import com.seiko.okhttp.flow.model.Location
import com.seiko.okhttp.flow.model.Name
import com.seiko.okhttp.flow.model.response.BannerResponse
import com.seiko.okhttp.flow.model.response.ListResponse
import com.seiko.okhttp.flow.model.response.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

class CoroutineViewModel : ViewModel() {

  val body = mutableStateOf("")

  fun sendGet() {
    NetHttp
      .get("article/list/0/json")
      .asFlow<Response<Page<ListResponse>>>()
      .map { it.data!!.datas[0].toString() }
      .catch { Timber.w(it) }
      .onEach { showBody(it) }
      .launchIn(viewModelScope)
  }

  fun sendPostForm() {
    NetHttp
      .postForm("article/query/0/json") {
        add("k", "性能优化")
      }
      .asFlowResponse<Page<ListResponse>>()
      .map { it.datas[1].toString() }
      .flowOn(Dispatchers.IO)
      .catch { Timber.w(it) }
      .onEach { showBody(it) }
      .launchIn(viewModelScope)
  }

  fun sendPostJson() {
    GsonNetHttp
      .postJson("banner/json") {
        add("name", "张三")
        add("sex", 1)
        addJsonObject("{\"height\":180,\"weight\":70}")
        add("interest", listOf("羽毛球", "游泳")) //添加数组对象
        add("location", Location(120.6788, 30.7866))  //添加位置对象
        addJsonElement("address", "{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"}")
      }
      .asFlowResponse<List<BannerResponse>>()
      .map { it[0].toString() }
      .flowOn(Dispatchers.IO)
      .catch { Timber.w(it) }
      .onEach { showBody(it) }
      .launchIn(viewModelScope)
  }

  fun sendPostJsonArray() {
    GsonNetHttp
      .postJson("banner/json") {
        body(listOf(Name("赵六"), Name("杨七"), mapOf("name" to "李四")))
      }
      .asFlow<Response<List<BannerResponse>>>()
      .map { it.data!![1].toString() }
      .flowOn(Dispatchers.IO)
      .catch { Timber.w(it) }
      .onEach { showBody(it) }
      .launchIn(viewModelScope)
  }

  fun fastJsonParse() {
    NetHttp
      .setConverter(Global.fastJsonConverter)
      .get("banner/json")
      .asFlowResponse<List<BannerResponse>>()
      .map { it[2].toString() }
      .flowOn(Dispatchers.IO)
      .catch { Timber.w(it) }
      .onEach { showBody(it) }
      .launchIn(viewModelScope)
  }

  private fun showBody(body: String) {
    this.body.value = body
  }
}
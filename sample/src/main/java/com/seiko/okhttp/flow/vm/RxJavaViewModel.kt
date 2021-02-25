package com.seiko.okhttp.flow.vm

import androidx.compose.runtime.mutableStateOf
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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class RxJavaViewModel : BaseRxViewModel() {

  val body = mutableStateOf("")

  fun sendGet() {
    RxNetHttp
      .get("/article/list/0/json") {
        add("aa", 111)
        add("bb", 222)
      }
      .asSingle<Response<Page<ListResponse>>>()
      .map { it.data!!.datas[0].toString() }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ showBody(it) }, { Timber.w(it) })
      .addToDisposables()
  }

  fun sendPostForm() {
    RxNetHttp
      .postForm("/article/query/0/json") {
        add("k", "性能优化")
      }
      .asSingleResponse<Page<ListResponse>>()
      .map { it.datas[1].toString() }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ showBody(it) }, { Timber.w(it) })
      .addToDisposables()
  }

  fun sendPostJson() {
    MyRxNetHttp
      .postJson("/banner/json") {
        add("name", "张三")
        add("sex", 1)
        addJsonObject("{\"height\":180,\"weight\":70}")
        add("interest", listOf("羽毛球", "游泳")) //添加数组对象
        add("location", Location(120.6788, 30.7866))  //添加位置对象
        addJsonElement("address", "{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"}")
      }
      .asSingleResponse<List<BannerResponse>>()
      .map { it[0].toString() }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ showBody(it) }, { Timber.w(it) })
      .addToDisposables()
  }

  fun sendPostJsonArray() {
    GsonNetHttp
      .postJson("/banner/json") {
        body(
          listOf(
            Name("赵六"),
            Name("杨七"),
            mapOf("name" to "李四"),
          )
        )
      }
      .asSingle<Response<List<BannerResponse>>>()
      .map { it.data!![1].toString() }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ showBody(it) }, { Timber.w(it) })
      .addToDisposables()
  }

  fun fastJsonParse() {
    NetHttp
      .setConverter(Global.fastJsonConverter)
      .setScheduler(Schedulers.computation())
      .get("/banner/json")
      .asSingleResponse<List<BannerResponse>>()
      .map { it[2].toString() }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ showBody(it) }, { Timber.w(it) })
      .addToDisposables()
  }

  private fun showBody(body: String) {
    this.body.value = body
  }
}
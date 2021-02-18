package com.seiko.okhttp.flow.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.seiko.net.NetHttp
import com.seiko.net.param.get
import com.seiko.net.param.post
import com.seiko.net.parser.toConvert
import com.seiko.okhttp.flow.R
import com.seiko.okhttp.flow.http.*
import com.seiko.okhttp.flow.model.TestData
import com.seiko.okhttp.flow.model.response.BannerResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener {

  private lateinit var textBody: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    textBody = findViewById(R.id.body)
    findViewById<Button>(R.id.btn_flow).setOnClickListener(this)
    findViewById<Button>(R.id.btn_rx).setOnClickListener(this)
    findViewById<Button>(R.id.btn_response).setOnClickListener(this)
    findViewById<Button>(R.id.btn_parse).setOnClickListener(this)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.btn_flow -> clickFlow()
      R.id.btn_rx -> clickRx()
      R.id.btn_response -> clickResponse()
      R.id.btn_parse -> clickParse()
    }
  }

  private fun clickFlow() {
    NetHttp.get("banner/json")
      .add("a", "aaa")
      .asFlow<Response<List<BannerResponse>>>()
      .onEach { showBody(it.data?.get(0).toString()) }
      .launchIn(lifecycleScope)
  }

  private fun clickRx() {
    GsonNetHttp.post("banner/json")
      .add("a", "aaa")
      .add("b", TestData(1, "20"))
      .add("c", listOf("1", "2", "3"))
      .asSingle<Response<List<BannerResponse>>>()
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe { it, _ -> showBody(it.data?.get(1).toString()) }
  }

  private fun clickResponse() {
    NetHttp.post("banner/json")
      .body(TestData(11, "22"))
      .asFlowResponse<List<BannerResponse>>()
      .onEach { showBody(it[2].toString()) }
      .launchIn(lifecycleScope)
  }

  private fun clickParse() {
    lifecycleScope.launch {
      val response = withContext(Dispatchers.IO) {
        NetHttp.post("banner/json")
          .add("a", "aaa")
          .add("b", TestData(1, "20"))
          .add("c", listOf("1", "2", "3"))
          .toConvert<Response<List<BannerResponse>>>()
      }
      showBody(response.data?.get(3).toString())
    }
  }

  private fun showBody(body: String) {
    textBody.text = body
  }

  private fun toast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
  }
}
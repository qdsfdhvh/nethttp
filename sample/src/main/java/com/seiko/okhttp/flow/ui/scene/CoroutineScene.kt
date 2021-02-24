package com.seiko.okhttp.flow.ui.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seiko.okhttp.flow.ui.widget.MyBtn
import com.seiko.okhttp.flow.vm.CoroutineViewModel

@Composable
fun CoroutineScene() {
  val viewModel: CoroutineViewModel = viewModel()
  val body by viewModel.body.observeAsState("")
  Surface(
    modifier = Modifier.fillMaxSize()
  ) {
    Column {
      LazyRow {
        item {
          MyBtn("Get") { viewModel.sendGet() }
          MyBtn("PostForm") { viewModel.sendPostForm() }
          MyBtn("PostJson") { viewModel.sendPostJson() }
          MyBtn("PostJsonArray") { viewModel.sendPostJsonArray() }
          MyBtn("FastJsonConverter") { viewModel.fastJsonParse() }
        }
      }
      Text(
        text = body,
        modifier = Modifier.padding(6.dp),
      )
    }
  }
}
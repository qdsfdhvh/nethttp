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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seiko.okhttp.flow.ui.widget.MyBtn
import com.seiko.okhttp.flow.vm.DownloadViewModel

@Composable
fun DownloadScene() {
  val viewModel: DownloadViewModel = viewModel()
  val body by viewModel.body.observeAsState("")
  val context = LocalContext.current
  Surface(
    modifier = Modifier.fillMaxSize()
  ) {
    Column {
      LazyRow {
        item {
          MyBtn("Rx下载") { viewModel.downloadRx(context) }
          MyBtn("Flow下载") { viewModel.downloadFlow(context) }
        }
      }
      Text(
        text = body,
        modifier = Modifier.padding(6.dp),
      )
    }
  }
}
package com.seiko.okhttp.flow.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seiko.okhttp.flow.ui.scene.CoroutineScene
import com.seiko.okhttp.flow.ui.scene.DownloadScene
import com.seiko.okhttp.flow.ui.scene.RxJavaScene
import com.seiko.okhttp.flow.ui.scene.UploadScene
import com.seiko.okhttp.flow.vm.MainViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        MainScene(viewModel)
      }
    }
  }
}

@Composable
private fun MainScene(
  viewModel: MainViewModel
) {
  val currentTab by viewModel.tab.observeAsState(Tabs.RxJava)
  Column(
    modifier = Modifier.background(Color.White),
  ) {
    TabRow(
      selectedTabIndex = currentTab.ordinal,
    ) {
      Tabs.values().forEachIndexed { index, tab ->
        Tab(
          selected = index == tab.ordinal,
          onClick = { viewModel.setTab(tab) },
        ) {
          Text(
            text = tab.name,
            modifier = Modifier.padding(vertical = 16.dp),
          )
        }
      }
    }
    when (currentTab) {
      Tabs.RxJava -> RxJavaScene()
      Tabs.Coroutine -> CoroutineScene()
      Tabs.Upload -> UploadScene()
      Tabs.Download -> DownloadScene()
    }
  }
}

enum class Tabs {
  RxJava,
  Coroutine,
  Upload,
  Download;
}

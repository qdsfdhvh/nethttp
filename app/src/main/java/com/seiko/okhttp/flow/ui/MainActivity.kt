package com.seiko.okhttp.flow.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
  val body by viewModel.body.observeAsState("")

  val context = LocalContext.current

  Surface(
    modifier = Modifier
      .background(Color.White)
      .fillMaxSize()
  ) {
    Column {
      Row {
        MyTextButton("Flow") { viewModel.clickFlow() }
        MyTextButton("Rx") { viewModel.clickRx() }
        MyTextButton("Response") { viewModel.clickResponse() }
        MyTextButton("Parse") { viewModel.clickParse() }
      }
      Row {
        MyTextButton("Download-Flow") {
          viewModel.clickDownloadFlow(context)
        }
        MyTextButton("Download-Rx") {
          viewModel.clickDownloadRx(context)
        }
      }
      Text(
        text = body,
        modifier = Modifier.padding(6.dp)
      )
    }
  }
}

@Composable
private fun MyTextButton(
  text: String,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier.padding(6.dp)
  ) {
    Text(text = text)
  }
}

@Preview(showBackground = true)
@Composable
private fun MyTextButtonPreview() {
  MyTextButton(
    text = "按钮",
    onClick = {}
  )
}
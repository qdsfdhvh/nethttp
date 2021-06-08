package com.seiko.okhttp.flow.ui.scene

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seiko.okhttp.flow.ui.widget.MyBtn
import com.seiko.okhttp.flow.ui.widget.Toast
import com.seiko.okhttp.flow.util.registerForActivityResult
import com.seiko.okhttp.flow.vm.UploadViewModel

@Composable
fun UploadScene() {
  val viewModel: UploadViewModel = viewModel()
  val context = LocalContext.current
  val launcher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
    uri ?: return@registerForActivityResult
    viewModel.upload(context, uri)
  }
  Surface(
    modifier = Modifier.fillMaxSize()
  ) {
    Row {
      MyBtn("选择文件") { launcher.launch(arrayOf("*/*")) }
    }
    Toast(viewModel.showMsg.value, viewModel.toastState)
  }
}
package com.seiko.okhttp.flow.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * thanks [JueJin](https://juejin.cn/post/6931932235553570823)
 */
@Composable
fun MyBtn(
  text: String,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier.padding(6.dp),
  ) {
    Text(text = text)
  }
}

@Preview(showBackground = true)
@Composable
private fun MyBtnPreview() {
  MyBtn(
    text = "按钮",
    onClick = {}
  )
}
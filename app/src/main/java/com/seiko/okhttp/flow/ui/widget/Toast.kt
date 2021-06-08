package com.seiko.okhttp.flow.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*

/**
 * thanks [github](https://github.com/jakepurple13/desktop-template/blob/master/examples/imageviewer/common/src/desktopMain/kotlin/example/imageviewer/view/Toast.kt)
 */
private val ToastBackground = Color(23, 23, 23)
private val Foreground = Color(210, 210, 210)

enum class ToastDuration(val value: Int) {
  Short(1000), Long(3000)
}

private var isShown: Boolean = false

@Composable
fun Toast(
  text: String,
  visibility: MutableState<Boolean> = mutableStateOf(false),
  duration: ToastDuration = ToastDuration.Short
) {
  if (isShown) {
    return
  }

  if (visibility.value) {
    isShown = true

    val key = rememberSaveable { UUID.randomUUID().toString() }

    Box(
      modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
      contentAlignment = Alignment.BottomCenter
    ) {
      Surface(
        modifier = Modifier.size(200.dp, 60.dp),
        color = ToastBackground,
        shape = RoundedCornerShape(4.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = text,
            color = Foreground
          )
        }

        LaunchedEffect(key) {
          delay(duration.value.toLong())
          isShown = false
          visibility.value = false
        }

        DisposableEffect(key) {
          onDispose {
            isShown = false
            visibility.value = false
          }
        }
      }
    }
  }
}
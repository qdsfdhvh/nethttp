package com.seiko.okhttp.flow.util

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import java.util.*

/**
 * thanks [stackoverflow](https://stackoverflow.com/questions/64721218/jetpack-compose-launch-activityresultcontract-request-from-composable-function)
 */
@Composable
fun <I, O> registerForActivityResult(
  contract: ActivityResultContract<I, O>,
  onResult: (O) -> Unit
): ActivityResultLauncher<I> {
  val owner = LocalContext.current as ActivityResultRegistryOwner
  val activityResultRegistry = owner.activityResultRegistry

  val currentOnResult = rememberUpdatedState(onResult)

  val key = rememberSaveable { UUID.randomUUID().toString() }

  val realLauncher = remember<ActivityResultLauncher<I>> {
    activityResultRegistry.register(key, contract) {
      currentOnResult.value(it)
    }
  }
  DisposableEffect(key) {
    onDispose {
      realLauncher.unregister()
    }
  }
  return realLauncher
}
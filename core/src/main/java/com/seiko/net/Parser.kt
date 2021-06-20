package com.seiko.net

import okhttp3.Response

interface Parser<out T> {
  fun onParse(response: Response): T
}


package com.seiko.net.download

import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface FlowDownloader {
  fun download(task: Task, response: Response): Flow<Progress>
}
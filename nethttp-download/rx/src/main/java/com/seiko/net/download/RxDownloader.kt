package com.seiko.net.download

import io.reactivex.rxjava3.core.Flowable
import okhttp3.Response

interface RxDownloader {
  fun download(task: Task, response: Response): Flowable<Progress>
}

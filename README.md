# okhttp-flow

[![version](https://api.bintray.com/packages/qdsfdhvh/maven/NetHttp/images/download.svg)](https://bintray.com/qdsfdhvh/maven/NetHttp)

参照[okhttp-RxHttp](https://github.com/liujingxing/okhttp-RxHttp)、[RxDownload](https://github.com/ssseasonnn/RxDownload)，按个人使用习惯编写的库，building...

## Getting started

in `build.gradle`:

```groovy
dependencies {
    def netHttpVersion = '1.0.0'
    implementation "com.seiko.net:nethttp:$netHttpVersion"
    // Ext
    implementation "com.seiko.net:nethttp-ext-flow:$netHttpVersion"
    implementation "com.seiko.net:nethttp-ext-rx:$netHttpVersion"
    // Converter
    implementation "com.seiko.net:nethttp-converter-moshi:$netHttpVersion"
    implementation "com.seiko.net:nethttp-converter-gson:$netHttpVersion"
    implementation "com.seiko.net:nethttp-converter-fastjson:$netHttpVersion"
    // Download
    implementation "com.seiko.net:nethttp-download-flow:$netHttpVersion"
    implementation "com.seiko.net:nethttp-download-rx:$netHttpVersion"
}
```

## Usage

Initialize the SDK

```kotlin
NetHttp.init(Global.okHttpClient, Global.moshiConverter)
NetHttp.baseUrl = "https://.../"
```

```kotlin
// rx
NetHttp
  .get("get/path") {
      add('k', 'v')
  }
  .add('k', 'v') // or here
  .asSingle<Response<Page<ListResponse>>>()
  .map { it.toString() }
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe({ showBody(it) }, { Timber.w(it) })

// flow
NetHttp
  .postJson("post/path") {
      add('k', 'v')
      body(Any)
  }
  .add('k', 'v') // or here
  .body(Any)
  .asFlow<Response<Page<ListResponse>>>()
  .map { it.toString() }
  .catch { Timber.w(it) }
  .onEach { showBody(it) }
  .launchIn(viewModelScope)

// more
object GsonNetHttp : NetHttp /* by NetHttp */ {
  override fun converter(): Converter = Global.gsonConverter
}

GsonNetHttp
  .get()
  ...


object MyFlowNetHttp : FlowNetHttp /* , NetHttp by NetHttp */ {
  override fun dispatcher(): CoroutineDispatcher = Dispatchers.Default
}

MyFlowNetHttp
  .get()
  .asFlow<Any>()
  ...


NetHttp
  .setConverter(converter)
  .setDispatcher(Dispatchers.Default) // or .setScheduler(Schedulers.computation())
  .get()
  ...

// download
NetHttp
  .download(
    url = "https://....apk",
    savePath = context.defaultDownloadDir(),
  )
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe { progress ->
    // progress.downloadSize
    // progress.downloadSizeFormat
    // progress.totalSize
    // progress.totalSizeFormat
    // progress.percent
    // progress.percentFormat
  }
```

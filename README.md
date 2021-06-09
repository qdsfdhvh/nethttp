# okhttp-flow

[![](https://jitpack.io/v/qdsfdhvh/nethttp.svg)](https://jitpack.io/#qdsfdhvh/nethttp)

参照[okhttp-RxHttp](https://github.com/liujingxing/okhttp-RxHttp)、[RxDownload](https://github.com/ssseasonnn/RxDownload)，按个人使用习惯编写的库，building...

## Getting started

in `build.gradle`:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    def netHttpVersion = '1.0.4'
    implementation "com.github.qdsfdhvh.nethttp:core:$netHttpVersion"
    // Ext
    implementation "com.github.qdsfdhvh.nethttp:ext-flow:$netHttpVersion"
    implementation "com.github.qdsfdhvh.nethttp:ext-rx:$netHttpVersion"
    // Converter
    implementation "com.github.qdsfdhvh.nethttp:converter-moshi:$netHttpVersion"
    implementation "com.github.qdsfdhvh.nethttp:converter-gson:$netHttpVersion"
    implementation "com.github.qdsfdhvh.nethttp:converter-fastjson:$netHttpVersion"
    // Download
    implementation "com.github.qdsfdhvh.nethttp:download-flow:$netHttpVersion"
    implementation "com.github.qdsfdhvh.nethttp:download-rx:$netHttpVersion"
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
    addQuery('k', 'v') 
    addHeader('k', 'v')
  }
  .addQuery('k', 'v') // or here
  .addHeader('k', 'v')
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

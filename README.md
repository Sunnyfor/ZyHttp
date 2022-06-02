[![](https://jitpack.io/v/Sunnyfor/ZyHttp.svg)](https://jitpack.io/#Sunnyfor/ZyHttp)

#### 1. 添加 jitpack 支持：项目级 build.gradle 中
```
maven { url "https://jitpack.io" }
```

#### 2. 添加 ZyFrame 框架依赖：应用级 build.gradle 中
```
implementation  com.github.Sunnyfor:ZyHttp:Tag
```

#### 3. 下期优化 2022/6/2
```
1）下载进度类优化：ProgressResponseBody，内存进度50%，硬盘进度50%，硬盘下载快，内存走网络慢，大文件下载会感觉不协调
2）下载地址获取：DefaultResponseParser中需要TEMP，ZyRequest中需要上下文
3）缓存 ZyCookieJar
4）占位图优化：数据为空，或者网络错误
```

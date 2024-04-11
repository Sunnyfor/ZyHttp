package com.sunny.http.interceptor

import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.body.ProgressResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc 拦截网络请求，获取下载进度
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
class ResponseProgressInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val downLoadResultBean = originalResponse.request.tag(DownLoadResultBean::class.java)
        val progressResponseListener = object : ProgressResponseBody.ProgressResponseListener {
            override fun onResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean) {
                downLoadResultBean?.let {
                    it.contentLength = contentLength
                    it.readLength = bytesRead
                    it.downloadDone = done
                    if (done) {
                        it.downloadEndTimeMillis = System.currentTimeMillis()
                    }

                    if (contentLength > 0L) {
                        it.progress = (bytesRead * 100L / contentLength).toInt()
                    }

                    val lastDataUpdateTimeMillis = it.attachment["lastDataUpdateTimeMillis"] as? Long ?: 0L
                    val currentTimeMillis = System.currentTimeMillis()

                    if (currentTimeMillis - lastDataUpdateTimeMillis >= 500 || done) {
                        it.attachment["lastDataUpdateTimeMillis"] = currentTimeMillis
                        it.scope?.launch(Dispatchers.Main) {
                            it.onDownloadProgressListener?.invoke(it)
                        }
                    }
                }
            }
        }
        val body = ProgressResponseBody(originalResponse.body, progressResponseListener)
        return originalResponse.newBuilder().body(body).build()
    }
}
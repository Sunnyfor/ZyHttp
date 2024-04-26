package com.sunny.http.interceptor

import com.sunny.http.bean.BaseHttpResultBean
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
        val httpResultBean = originalResponse.request.tag(BaseHttpResultBean::class.java)
        if (httpResultBean is DownLoadResultBean) {
            val progressResponseListener = object : ProgressResponseBody.ProgressResponseListener {
                override fun onProgress(bytesRead: Long, contentLength: Long) {
                    httpResultBean.contentLength = contentLength
                    httpResultBean.readLength = bytesRead
                    httpResultBean.downloadDone = bytesRead == contentLength

                    if (httpResultBean.downloadDone) {
                        httpResultBean.downloadEndTimeMillis = System.currentTimeMillis()
                    }

                    if (contentLength > 0L) {
                        val lastProgress = httpResultBean.progress
                        httpResultBean.progress = (bytesRead * 100L / contentLength).toInt()
                        val lastUpdateTimeMillis = httpResultBean.attachment["lastUpdateTimeMillis"] as? Long ?: 0L
                        val currentTimeMillis = System.currentTimeMillis()
                        if (currentTimeMillis - lastUpdateTimeMillis > 500 && httpResultBean.progress != lastProgress) {
                            httpResultBean.attachment["lastUpdateTimeMillis"] = currentTimeMillis
                            httpResultBean.scope?.launch(Dispatchers.Main) {
                                httpResultBean.onDownloadProgress(httpResultBean)
                            }
                        }
                    }
                }
            }
            originalResponse.body?.let { responseBody ->
                val body = ProgressResponseBody(responseBody, progressResponseListener)
                return originalResponse.newBuilder().body(body).build()
            }
        }
        return originalResponse
    }
}
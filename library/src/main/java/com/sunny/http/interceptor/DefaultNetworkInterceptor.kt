package com.sunny.http.interceptor

import android.os.Handler
import android.os.Looper
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.body.ProgressResponseBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc 拦截网络请求，获取下载进度
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
class DefaultNetworkInterceptor : Interceptor {

    val handler = Handler(Looper.getMainLooper())

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val downLoadResultBean = originalResponse.request.tag(DownLoadResultBean::class.java)
        val body = ProgressResponseBody(
            originalResponse.body,
            object : ProgressResponseBody.ProgressResponseListener {
                override fun onResponseProgress(
                    bytesRead: Long,
                    contentLength: Long,
                    done: Boolean
                ) {
                    if (contentLength > 0L) {
                        downLoadResultBean?.let {
                            it.contentLength = contentLength
                            it.readLength = bytesRead
                            val progress = (bytesRead * 100L / contentLength).toInt()
                            if (progress != it.progress) {
                                it.progress = progress
                                handler.post {
                                    it.notifyData(it)
                                }
                            }
                        }
                    }
                }
            }
        )
        return originalResponse.newBuilder().body(body).build()
    }
}
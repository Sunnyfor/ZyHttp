package com.sunny.http.utils

import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.interceptor.DefaultNetworkInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Desc 创建OkHttpClient工厂类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
class OkHttpClientFactory {

    private fun getBuild(): OkHttpClient.Builder {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(ZyHttpConfig.headerInterceptor)
                .addNetworkInterceptor(ZyHttpConfig.logInterceptor)
                .sslSocketFactory(
                    ZySSLSocketClient.createSSLSocketFactory(),
                    ZySSLSocketClient.getTrustManager()
                )
                .hostnameVerifier(ZyHttpConfig.hostnameVerifier)
                .connectTimeout(ZyHttpConfig.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS) //连接超时时间
                .readTimeout(ZyHttpConfig.READ_TIME_OUT, TimeUnit.MILLISECONDS) //读取超时时间
//                .cookieJar(ZyHttpConfig.zyCookieJar)
                .retryOnConnectionFailure(true)

        ZyHttpConfig.extendInterceptor?.let {
            builder.addInterceptor(it)
        }
        return builder
    }

    /**
     * 创建新的Client对象
     */
    fun getOkHttpClient(): OkHttpClient {
        return getBuild().build()
    }

    /**
     * 创建附带下载进度的okHttpClient
     */
    fun createDownloadClient(downLoadResultBean: DownLoadResultBean): OkHttpClient {
        return getBuild().addNetworkInterceptor(DefaultNetworkInterceptor(downLoadResultBean)).build()
    }
}

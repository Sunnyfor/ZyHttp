package com.sunny.http.factory

import com.sunny.http.ZyHttpConfig
import com.sunny.http.interceptor.ResponseProgressInterceptor
import com.sunny.http.utils.ZySSLSocketClient
import com.sunny.kit.utils.application.glide.ZyAppGlideModule
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Desc 创建OkHttpClient工厂类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
class ZyOkHttpClientFactory : IOkHttpClientFactory {

    private var okHttpClient: OkHttpClient? = null

    /**
     * 创建新的Client对象
     */
    override fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = getBuild().build()
            ZyAppGlideModule.okHttpClient = okHttpClient as OkHttpClient //关联Glide
        }
        return okHttpClient as OkHttpClient
    }


    /**
     * 清空OkHttpClient
     */
    override fun onDestroy() {
        okHttpClient = null

    }

    /**
     * 获取OkHttpClient.Builder对象
     */
    override fun getBuild(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(ZyHttpConfig.headerInterceptor)
            .addNetworkInterceptor(ZyHttpConfig.logInterceptor)
            .addNetworkInterceptor(ResponseProgressInterceptor())
            .sslSocketFactory(
                ZySSLSocketClient.createSSLSocketFactory(),
                ZySSLSocketClient.getTrustManager()
            )
            .hostnameVerifier(ZyHttpConfig.hostnameVerifier)
            .connectTimeout(ZyHttpConfig.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS) //连接超时时间
            .readTimeout(ZyHttpConfig.READ_TIME_OUT, TimeUnit.MILLISECONDS) //读取超时时间
            .callTimeout(ZyHttpConfig.CALL_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(ZyHttpConfig.WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
            .cookieJar(ZyHttpConfig.zyCookieJar)
    }
}

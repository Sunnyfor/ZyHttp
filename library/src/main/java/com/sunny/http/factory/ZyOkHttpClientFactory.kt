package com.sunny.http.factory

import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.interceptor.DefaultNetworkInterceptor
import com.sunny.http.utils.ZySSLSocketClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Desc 创建OkHttpClient工厂类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
class ZyOkHttpClientFactory : IOkHttpClientFactory {

    private var basicOkHttpClient: OkHttpClient? = null

    private val networkInterceptor by lazy {
        DefaultNetworkInterceptor()
    }

    private fun init() {
        if (basicOkHttpClient == null) {
            basicOkHttpClient = getBuild()
                .addNetworkInterceptor(networkInterceptor)
                .build()
        }
    }

    /**
     * 创建新的Client对象
     */
    override fun getOkHttpClient(): OkHttpClient {
        init()
        return basicOkHttpClient as OkHttpClient
    }

    /**
     * 创建附带下载进度的okHttpClient
     */
    override fun getDownloadClient(downLoadResultBean: DownLoadResultBean): OkHttpClient {
        init()
        return basicOkHttpClient as OkHttpClient
    }


    override fun onDestroy() {
        basicOkHttpClient = null

    }

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

                .retryOnConnectionFailure(true)
        ZyHttpConfig.zyCookieJar?.let {
            builder.cookieJar(it)
        }
        ZyHttpConfig.extendInterceptor?.let {
            builder.addInterceptor(it)
        }
        return builder
    }
}

package com.sunny.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc Header拦截器
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/08/24
 */
class DefaultHeaderInterceptor : Interceptor {

    private var headerMap = hashMapOf<String, Any>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authorised = originalRequest.newBuilder()
        //全局头信息
        headerMap.forEach {
            authorised.header(it.key, it.value.toString())
        }
        return chain.proceed(authorised.build())
    }

    /**
     * 设置网络请求头信息
     */
    fun setHttpHeader(headerMap: HashMap<String, *>) {
        this.headerMap.clear()
        this.headerMap.putAll(headerMap)
    }

}
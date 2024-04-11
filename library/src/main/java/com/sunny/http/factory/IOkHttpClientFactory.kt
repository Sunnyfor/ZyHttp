package com.sunny.http.factory

import com.sunny.http.bean.DownLoadResultBean
import okhttp3.OkHttpClient

/**
 * Desc 创建OkHttpClient工厂类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
interface IOkHttpClientFactory {
    /**
     * 创建新的Client对象
     */
    fun getOkHttpClient(): OkHttpClient

    /**
     * 获取OkHttpClient.Builder对象
     */
    fun getBuild(): OkHttpClient.Builder

    /**
     * 清空OkHttpClient
     */
    fun onDestroy()
}

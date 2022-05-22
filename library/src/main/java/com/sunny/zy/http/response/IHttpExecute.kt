package com.sunny.zy.http.response

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import okhttp3.Request

/**
 * Desc 网络请求执行器接口
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/8/10
 */
interface IHttpExecute {
    fun executeDownload(request: Request, resultBean: DownLoadResultBean)
    fun <T> executeHttp(request: Request, resultBean: HttpResultBean<T>)
}
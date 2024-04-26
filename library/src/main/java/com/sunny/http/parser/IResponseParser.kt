package com.sunny.http.parser

import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.bean.HttpResultBean
import okhttp3.ResponseBody

/**
 * Desc 数据解析
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29
 */
interface IResponseParser {

    fun <T> parserHttpResponse(responseBody: ResponseBody, httpResultBean: HttpResultBean<T>): T

    fun parserDownloadResponse(responseBody: ResponseBody, downLoadResultBean: DownLoadResultBean)

}
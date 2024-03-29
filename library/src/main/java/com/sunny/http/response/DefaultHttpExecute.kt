package com.sunny.http.response

import android.webkit.MimeTypeMap
import com.sunny.http.ZyHttp
import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.bean.HttpResultBean
import okhttp3.Request


/**
 * Desc 默认的Http执行器，可重写
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/8/10
 */
open class DefaultHttpExecute : IHttpExecute {

    override fun executeDownload(request: Request, resultBean: DownLoadResultBean) {
        resultBean.call = ZyHttp.clientFactory.getDownloadClient(resultBean).newCall(request)
        resultBean.call?.execute()?.let { response ->
            //获取HTTP状态码
            resultBean.httpCode = response.code
            //获取Response回执信息
            resultBean.message = response.message
            //获取请求URL
            resultBean.url = request.url.toString()
            if (response.isSuccessful) {
                response.body?.let {
                    val contentType = response.headers["content-type"]
                    if (!contentType.isNullOrEmpty()) {
                        val mimeType = contentType.split(";")[0].trim()
                        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                        resultBean.extension = extension ?: ""
                    }
                    resultBean.file = ZyHttpConfig.iResponseParser.parserDownloadResponse(it, resultBean)
                }
            }
        }
    }

    override fun <T> executeHttp(request: Request, resultBean: HttpResultBean<T>) {
        resultBean.call = ZyHttp.clientFactory.getOkHttpClient().newCall(request)
        resultBean.call?.execute()?.let { response ->
            //获取HTTP状态码
            resultBean.httpCode = response.code
            //获取Response回执信息
            resultBean.message = response.message
            //获取响应URL
            resultBean.resUrl = response.request.url.toString()

            if (response.isSuccessful) {
                response.body?.let {
                    resultBean.bean = ZyHttpConfig.iResponseParser.parserHttpResponse(it, resultBean)
                }
            }
        }
    }

}
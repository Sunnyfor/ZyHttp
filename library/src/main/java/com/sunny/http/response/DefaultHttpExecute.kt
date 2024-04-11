package com.sunny.http.response

import android.webkit.MimeTypeMap
import com.sunny.http.ZyHttp
import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.bean.HttpResultBean
import okhttp3.Request
import java.net.URLDecoder


/**
 * Desc 默认的Http执行器，可重写
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/8/10
 */
open class DefaultHttpExecute : IHttpExecute {

    override fun executeDownload(request: Request, resultBean: DownLoadResultBean) {
        resultBean.call = ZyHttp.clientFactory.getOkHttpClient().newCall(request)
        resultBean.call?.execute()?.let { response ->
            //获取HTTP状态码
            resultBean.httpCode = response.code
            //获取Response回执信息
            resultBean.message = response.message
            //获取请求URL
            resultBean.url = request.url.toString()
            if (response.isSuccessful) {
                response.body?.let { body ->
                    response.header("Content-Disposition")?.let {
                        // 查找filename参数并解码
                        val startIndex = it.indexOf("filename=")
                        if (startIndex != -1) {
                            if (resultBean.fileName.isEmpty()) {
                                resultBean.fileName = URLDecoder.decode(it.substring(startIndex + "filename=".length).trim(), Charsets.UTF_8.name())
                            }
                        }
                    }
                    if (resultBean.fileName.isEmpty()) {

                        resultBean.fileName = resultBean.url.substring(response.request.url.toString().lastIndexOf("/") + 1).replace("?", "_")
                        resultBean.contentType = MimeTypeMap.getFileExtensionFromUrl(resultBean.fileName)
                    }
                    val contentType = response.headers["content-type"]?.takeIf { it.isNotEmpty() }
                    contentType?.split(";".toRegex())?.dropLastWhile { it.isEmpty() }?.first()?.trim()?.let {
                        resultBean.contentType = it
                        resultBean.extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(it) ?: ""
                    }
                    ZyHttpConfig.iResponseParser.parserDownloadResponse(body, resultBean)
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
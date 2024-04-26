package com.sunny.http.response

import android.net.Uri
import com.sunny.http.ZyHttp
import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.BaseHttpResultBean
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.bean.HttpResultBean
import okhttp3.Request
import okhttp3.Response


/**
 * Desc 默认的Http执行器，可重写
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/8/10
 */
open class DefaultHttpExecute : IHttpExecute {

    /**
     * 执行下载文件
     */
    override fun executeDownload(request: Request, resultBean: DownLoadResultBean) {
        resultBean.reset()
        resultBean.call = ZyHttp.clientFactory.getOkHttpClient().newCall(request)
        resultBean.call?.execute()?.let { response ->
            populateHttpResultBean(resultBean, response)
            if (response.isSuccessful) {
                response.body?.let { body ->
                    if (resultBean.fileName.isEmpty()) {
                        val contentDisposition = response.header("Content-Disposition")
                        if (contentDisposition != null) {
                            val matcher = Regex("filename=\"(.+?)\"").find(contentDisposition)
                            resultBean.fileName = Uri.decode(matcher?.groupValues?.get(1))
                        } else {
                            resultBean.fileName = resultBean.url.substringAfterLast("/").replace("?", "_")
                        }
                    }
                    val contentType = body.contentType()
                    if (contentType != null) {
                        resultBean.contentType = contentType.toString()
                    }
                    ZyHttpConfig.iResponseParser.parserDownloadResponse(body, resultBean)
                }
            }
        }
    }

    /**
     * 执行网络请求
     */
    override fun <T> executeHttp(request: Request, resultBean: HttpResultBean<T>) {
        resultBean.call = ZyHttp.clientFactory.getOkHttpClient().newCall(request)
        resultBean.call?.execute()?.let { response ->
            populateHttpResultBean(resultBean, response)
            if (response.isSuccessful) {
                response.body?.let {
                    resultBean.bean = ZyHttpConfig.iResponseParser.parserHttpResponse(it, resultBean)
                }
            }
        }
    }


    /**
     * 填充HttpResultBean
     */
    private fun populateHttpResultBean(httpResultBean: BaseHttpResultBean, response: Response) {
        //获取请求是否成功
        httpResultBean.httpIsSuccess = response.isSuccessful
        //获取HTTP状态码
        httpResultBean.httpCode = response.code
        //获取Response回执信息
        httpResultBean.message = response.message
        //获取响应URL
        httpResultBean.resUrl = Uri.decode(response.request.url.toString())
    }
}
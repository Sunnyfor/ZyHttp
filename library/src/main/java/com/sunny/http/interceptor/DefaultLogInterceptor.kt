package com.sunny.http.interceptor

import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.BaseHttpResultBean
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.kit.utils.application.ZyKit
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import okio.InflaterSource
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.zip.Inflater

/**
 * Desc Log日志拦截器
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/9/29
 */
class DefaultLogInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val connection = chain.connection()
        val startLogSb = StringBuilder()

        startLogSb.append("${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        startLogSb.append("\n")
        //头信息
        startLogSb.append(request.headers.joinToString("\n") { it.first + ": " + it.second })

        val params = request.tag().toString()
        if (params.isNotEmpty()) {
            startLogSb.append("\n")
            startLogSb.append("Params: $params")
        }
        ZyKit.log.w("Network Request", startLogSb.toString(), false)

        val endLogSb = StringBuilder()
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            endLogSb.append(e.message)
            ZyKit.log.w("Network Response", endLogSb.toString(), false)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.peekBody(ZyHttpConfig.RESPONSE_BODY_MAX_LOG_SIZE)
        val status = if (response.isSuccessful) "\uD83D\uDFE2" else "\uD83D\uDD34"
        endLogSb.append("${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms) $status")
        endLogSb.append("\n")

        //头信息
        endLogSb.append(response.headers.joinToString("\n") { it.first + ": " + it.second })
        endLogSb.append("\n")

        if (response.promisesBody()) {
            responseBody.source().let { bufferResource ->
                val httpResultBean = request.tag(BaseHttpResultBean::class.java)
                if (httpResultBean is DownLoadResultBean) {
                    endLogSb.append("${ZyKit.file.formatFileSize(responseBody.contentLength())} size body omitted")
                } else {
                    bufferResource.request(Long.MAX_VALUE)
                    val buffer = when (response.headers["Content-Encoding"]?.lowercase()) {
                        "gzip" -> {
                            // 如果是gzip压缩的，进行解压
                            GzipSource(bufferResource.buffer.clone()).use {
                                Buffer().apply {
                                    writeAll(it)
                                }
                            }
                        }

                        "deflate" -> {
                            // 如果是deflate压缩的，进行解压
                            InflaterSource(bufferResource.buffer.clone(), Inflater(true)).use {
                                Buffer().apply {
                                    writeAll(it)
                                }
                            }
                        }

                        else -> {
                            // 不是压缩的或未知的编码，直接使用原始缓冲区
                            bufferResource.buffer.clone()
                        }
                    }
                    responseBody.contentType()?.let {
                        if (it.type == "text" || it.type == "application") {
                            val result = buffer.readUtf8()
                            endLogSb.append("\n")
                            endLogSb.append(result.replace(Regex("[\\s\\n]+"), ""))
                        }
                    }
                }
            }
        }
        ZyKit.log.w("Network Response", endLogSb.toString(), false)
        return response
    }
}
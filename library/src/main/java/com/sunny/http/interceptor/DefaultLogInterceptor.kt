package com.sunny.http.interceptor

import com.sunny.http.ZyHttpConfig
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.kit.utils.application.ZyKit
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import okio.InflaterSource
import java.io.IOException
import java.nio.charset.StandardCharsets
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
        ZyKit.log.w("发起请求", startLogSb.toString(), false)

        val endLogSb = StringBuilder()
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            endLogSb.append(e.message)
            ZyKit.log.w("请求结束", endLogSb.toString(), false)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        endLogSb.append("${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)")
        endLogSb.append("\n")

        //头信息
        endLogSb.append(response.headers.joinToString("\n") { it.first + ": " + it.second })
        endLogSb.append("\n")

        if (response.promisesBody()) {
            responseBody?.source()?.let { bufferResource ->
                val downLoadResultBean = request.tag(DownLoadResultBean::class.java)
                if (downLoadResultBean != null) {
                    endLogSb.append("fileName：${downLoadResultBean.fileName} fileSize：${ZyKit.file.formatFileSize(responseBody.contentLength())} body omitted")
                } else {
                    val contentLength = responseBody.contentLength()
                    if (contentLength == -1L) {
                        endLogSb.append("unknown body size  omitted")
                    } else if (contentLength <= ZyHttpConfig.MAX_RESPONSE_BODY_SIZE) {
                        bufferResource.request(Long.MAX_VALUE)
                        val buffer = when (response.headers["Content-Encoding"]?.lowercase()) {
                            "gzip" -> {
                                // 如果是gzip压缩的，进行解压
                                bufferResource.request(Long.MAX_VALUE)
                                GzipSource(bufferResource.buffer.clone()).use {
                                    Buffer().apply {
                                        writeAll(it)
                                    }
                                }
                            }

                            "deflate" -> {
                                // 如果是deflate压缩的，进行解压
                                bufferResource.request(Long.MAX_VALUE)
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
                        val result = buffer.readString(StandardCharsets.UTF_8)
                        endLogSb.append(result.replace(Regex("[\\s\\n]+"), ""))
                    } else {
                        endLogSb.append("body size greater than ${ZyHttpConfig.MAX_RESPONSE_BODY_SIZE} omitted")
                    }
                }
            }
        }
        ZyKit.log.w("请求结束", endLogSb.toString(), false)
        return response
    }
}
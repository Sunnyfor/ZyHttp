package com.sunny.http.body

import com.sunny.kit.utils.application.ZyKit
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer

/**
 * Desc 下载进度
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/08/24
 */
class ProgressResponseBody(
    var responseBody: ResponseBody,
    var progressListener: ProgressResponseListener
) : ResponseBody() {

    //包装完成的BufferedSource
    private var bufferedSource: BufferedSource? = null

    override fun contentLength() = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            //当前读取字节数
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                return runCatching {
                    val bytesRead = super.read(sink, byteCount)
                    if (bytesRead != -1L) {
                        totalBytesRead += bytesRead
                        progressListener.onProgress(
                            totalBytesRead,
                            responseBody.contentLength()
                        )
                    }
                    bytesRead
                }.onFailure {
                    ZyKit.log.e("Exception", it.message ?: "")
                }.getOrDefault(-1)
            }
        }
    }


    interface ProgressResponseListener {
        fun onProgress(bytesRead: Long, contentLength: Long)
    }
}
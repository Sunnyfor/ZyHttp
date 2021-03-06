package com.sunny.http.body

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 * Desc 下载进度
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/08/24
 */
class ProgressResponseBody(
    var responseBody: ResponseBody?,
    var progressListener: ProgressResponseListener
) : ResponseBody() {

    //包装完成的BufferedSource
    var bufferedSource: BufferedSource? = null

    override fun contentLength() = responseBody?.contentLength() ?: 0

    override fun contentType(): MediaType? = responseBody?.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody?.source())?.buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source?): Source? {
        source?.let {
            return object : ForwardingSource(it) {
                //当前读取字节数
                var totalBytesRead = 0L
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)

                    if (bytesRead == -1L) {
                        return bytesRead
                    }
                    totalBytesRead += bytesRead
                    progressListener.onResponseProgress(
                        totalBytesRead,
                        responseBody?.contentLength() ?: 0,
                        totalBytesRead == responseBody?.contentLength()
                    )
                    return bytesRead
                }
            }
        }
        return null
    }


    interface ProgressResponseListener {
        fun onResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean)
    }
}
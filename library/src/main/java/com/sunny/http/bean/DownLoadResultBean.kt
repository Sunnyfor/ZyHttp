package com.sunny.http.bean

import android.net.Uri

/**
 * Desc 下载结果实体类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
abstract class DownLoadResultBean : BaseHttpResultBean() {

    var fileName: String = ""
    var uri: Uri? = null
    var contentType = "" //文件类型
    var contentLength = 0L //数据长度
    var readLength = 0L  //当前读取长度
    var downloadDone = false //网络传输完成状态
    var diskWriteDone = false //磁盘写入完成状态
    var progress = 0 //下载进度百分比
    var filePath = ""
    var downloadStartTimeMillis = System.currentTimeMillis() //开始下载时间
    var downloadEndTimeMillis = 0L //下载结束时间
    var diskWriteStartTimeMillis = 0L //磁盘写入开始时间
    var diskWriteEndTimeMillis = 0L //磁盘写入结束时间
    val attachment: MutableMap<String, Any> = HashMap() //附件

    /**
     * 判断下载是否完成
     */
    fun isDone() = downloadDone && diskWriteDone

    /**
     * 重置下载状态
     */
    fun reset() {
        contentType = ""
        contentLength = 0L
        readLength = 0L
        downloadDone = false
        diskWriteDone = false
        progress = 0
        downloadStartTimeMillis = System.currentTimeMillis()
        downloadEndTimeMillis = 0L
        diskWriteStartTimeMillis = 0L
        diskWriteEndTimeMillis = 0L
        attachment.clear()
    }

    /**
     * 获取下载时长
     */
    fun getDownloadDuration(): Long {
        return if (downloadEndTimeMillis == 0L) {
            0
        } else {
            downloadEndTimeMillis - downloadStartTimeMillis
        }
    }

    /**
     * 获取磁盘写入时长
     */
    fun getDiskWriteDuration(): Long {
        return if (diskWriteEndTimeMillis == 0L) {
            0
        } else {
            diskWriteEndTimeMillis - diskWriteStartTimeMillis
        }
    }


    override fun toString(): String {
        return "DownLoadResultBean(${super.toString()}, fileName='$fileName', uri=$uri, contentLength=$contentLength, readLength=$readLength, networkTransferDone=$downloadDone, diskWriteDone=$diskWriteDone, progress=$progress, filePath='$filePath', downloadStartTimeMillis=$downloadStartTimeMillis, downloadEndTimeMillis=$downloadEndTimeMillis, diskWriteStartTimeMillis=$diskWriteStartTimeMillis, diskWriteEndTimeMillis=$diskWriteEndTimeMillis)"
    }

    abstract fun onDownloadProgress(downLoadResultBean: DownLoadResultBean)
}
package com.sunny.http.bean

import android.net.Uri
import com.sunny.kit.utils.application.ZyKit
import java.util.UUID

/**
 * Desc 下载结果实体类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24
 */
class DownLoadResultBean() :
    BaseHttpResultBean() {

    constructor(fileName: String, uri: Uri?) : this() {
        this.fileName = fileName
        this.uri = uri
    }

    constructor(fileName: String, filePath: String) : this() {
        this.fileName = fileName
        this.filePath = filePath
    }


    val id = UUID.randomUUID().toString()
    var fileName: String = ""
    var extension = "unknown" //文件扩展名
    var uri: Uri? = null
    var contentType = "" //文件类型
    var contentLength = 0L //数据长度
    var readLength = 0L  //当前读取长度
    var downloadDone = false //网络传输完成状态
    var diskWriteDone = false //磁盘写入完成状态
    var progress = 0 //下载进度百分比
    var filePath = ZyKit.file.getExternalFilesDir("download")
    var downloadStartTimeMillis = System.currentTimeMillis() //开始下载时间
    var downloadEndTimeMillis = 0L //下载结束时间
    var diskWriteStartTimeMillis = 0L //磁盘写入开始时间
    var diskWriteEndTimeMillis = 0L //磁盘写入结束时间
    var onDownloadProgressListener: ((resultBean: DownLoadResultBean) -> Unit)? = null //下载进度监听

    var attachment: MutableMap<String, Any> = HashMap() //附件

    fun isDone() = downloadDone && diskWriteDone

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


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownLoadResultBean

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "DownLoadResultBean(${super.toString()},id='$id', fileName='$fileName', extension='$extension', uri=$uri, contentLength=$contentLength, readLength=$readLength, networkTransferDone=$downloadDone, diskWriteDone=$diskWriteDone, progress=$progress, filePath='$filePath', downloadStartTimeMillis=$downloadStartTimeMillis, downloadEndTimeMillis=$downloadEndTimeMillis, diskWriteStartTimeMillis=$diskWriteStartTimeMillis, diskWriteEndTimeMillis=$diskWriteEndTimeMillis)"
    }

}
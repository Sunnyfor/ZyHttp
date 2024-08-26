package com.sunny.http.parser

import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.bean.HttpResultBean
import com.sunny.kit.utils.application.ZyKit
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Desc 默认解析器，可重写
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29
 */
@Suppress("UNCHECKED_CAST")
open class DefaultResponseParser : IResponseParser {

    open val mGSon = Gson()

    override fun <T> parserHttpResponse(
        responseBody: ResponseBody,
        httpResultBean: HttpResultBean<T>
    ): T {

        val type = httpResultBean.dataType
        val body = responseBody.string()

        if (type.toString() == String::class.java.toString()) {
            return body as T
        }

        //解析泛型类
        return mGSon.fromJson(body, type)
    }

    override fun parserDownloadResponse(
        responseBody: ResponseBody,
        downLoadResultBean: DownLoadResultBean
    ) {
        writeResponseBodyToDisk(responseBody.byteStream(), downLoadResultBean)
    }


    /**
     * 文件写入SD卡
     */
    private fun writeResponseBodyToDisk(
        data: InputStream,
        downLoadResultBean: DownLoadResultBean
    ) {

        //初始化文件名
        val contentUri = downLoadResultBean.uri

        val outputStream = if (contentUri == null) {
            if (downLoadResultBean.filePath.isEmpty()) {
                downLoadResultBean.filePath = ZyKit.file.getExternalFilesDir("download")
            }
            val pathFile = File(downLoadResultBean.filePath)
            if (!pathFile.exists()) {
                pathFile.mkdirs()
            }

            val file = File(pathFile, downLoadResultBean.fileName)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            FileOutputStream(file)
        } else {
            val parentDocumentFile = DocumentFile.fromTreeUri(ZyKit.getContext(), contentUri)
            parentDocumentFile?.findFile(downLoadResultBean.fileName)?.delete()
            parentDocumentFile?.createFile("", downLoadResultBean.fileName)?.let {
                ZyKit.getContext().contentResolver.openOutputStream(it.uri)
            }
        }

        val byte = ByteArray(4096)
        var totalRead = 0L
        downLoadResultBean.scope?.launch(IO) {
            runCatching {
                downLoadResultBean.diskWriteStartTimeMillis = System.currentTimeMillis()
                //写入文件
                while (true) {
                    val read = data.read(byte)
                    if (read == -1) {
                        break
                    }
                    totalRead += read
                    outputStream?.write(byte, 0, read)
                }
                outputStream?.flush()
                outputStream?.close()
                downLoadResultBean.diskWriteDone = true
                downLoadResultBean.diskWriteEndTimeMillis = System.currentTimeMillis()
                withContext(Main) {
                    downLoadResultBean.onDownloadProgress(downLoadResultBean)
                }
            }.onFailure {
                ZyKit.log.e("Exception", it.message ?: "")
            }
        }
    }

}
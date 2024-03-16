package com.sunny.http.parser

import com.google.gson.Gson
import com.sunny.http.bean.DownLoadResultBean
import com.sunny.http.bean.HttpResultBean
import com.sunny.kit.utils.FileUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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

        val type = httpResultBean.type
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
    ): File {
        return writeResponseBodyToDisk(responseBody.byteStream(), downLoadResultBean)
    }


    /**
     * 文件写入SD卡
     */
    private fun writeResponseBodyToDisk(
        data: InputStream,
        downLoadResultBean: DownLoadResultBean
    ): File {

        if (downLoadResultBean.filePath == null) {
            downLoadResultBean.filePath = FileUtil.getCacheDir()
        }

        val pathFile = File(downLoadResultBean.filePath ?: "")
        if (!pathFile.exists()) {
            pathFile.mkdirs()
        }

        var fileName = downLoadResultBean.fileName ?: ""
        if (fileName.isEmpty()) {
            fileName = "${System.currentTimeMillis()}.${downLoadResultBean.extension}"
        } else {
            if (!fileName.contains(".")) {
                fileName += ".${downLoadResultBean.extension}"
            }
        }
        downLoadResultBean.fileName = fileName
        val file = File(pathFile, fileName)
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()

        val byte = ByteArray(4096)
        val outputStream = FileOutputStream(file)
        var totalRead = 0L
        downLoadResultBean.scope?.launch(IO) {
            //写入文件
            while (true) {
                val read = data.read(byte)
                if (read == -1) {
                    break
                }
                totalRead += read
                outputStream.write(byte, 0, read)
            }
            downLoadResultBean.done = totalRead == downLoadResultBean.readLength
        }
        outputStream.flush()
        return file
    }

}
package com.sunny.http.bean

import kotlinx.coroutines.CoroutineScope
import okhttp3.Call
import java.util.UUID

abstract class BaseHttpResultBean {
    val id = UUID.randomUUID().toString()
    var url = "" //请求URL
    var resUrl = ""     //响应URL
    var httpCode = 0    //请求code
    var message = ""    //响应消息
    var httpIsSuccess = false
    var call: Call? = null
    var scope: CoroutineScope? = null
    var exception: Throwable? = null //网络请求异常信息

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseHttpResultBean

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "id=$id, url='$url', resUrl='$resUrl', httpCode=$httpCode, message='$message', httpIsSuccess=$httpIsSuccess, "
    }
}
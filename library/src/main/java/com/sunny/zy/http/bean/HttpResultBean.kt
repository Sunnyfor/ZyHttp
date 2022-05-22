package com.sunny.zy.http.bean

import com.sunny.zy.utils.LogUtil
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc 网络请求结果实体类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29
 */
abstract class HttpResultBean<T> : BaseHttpResultBean() {

    lateinit var type: Type

    init {
        javaClass.genericSuperclass?.let {
            if (it is ParameterizedType) {
                type = it.actualTypeArguments[0]
            }
        }
    }

    var bean: T? = null

    fun isSuccess(): Boolean {
        if (httpIsSuccess()) {
            if (message.isEmpty() || message == "OK")
                return true
        }
        LogUtil.e(message)
        return false
    }

    override fun toString(): String {
        return "${super.toString()} HttpResultBean(typeToken=$type, bean=$bean)"
    }

}
package com.sunny.http.bean

import com.sunny.http.utils.HttpLogUtil
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

    fun isSuccess(flag:Boolean = true):Boolean{
        return httpIsSuccess() && flag
    }

    override fun toString(): String {
        return "${super.toString()} HttpResultBean(typeToken=$type, bean=$bean)"
    }

}
package com.sunny.http.bean

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc 网络请求结果实体类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29
 */
abstract class HttpResultBean<T> : BaseHttpResultBean() {

    lateinit var dataType: Type

    init {
        javaClass.genericSuperclass?.let {
            if (it is ParameterizedType) {
                dataType = it.actualTypeArguments[0]
            }
        }
    }

    var data: T? = null

    fun isSuccess(flag:Boolean = true):Boolean{
        return httpIsSuccess && flag
    }

    override fun toString(): String {
        return "HttpResultBean(${super.toString()}dataType=$dataType, data=$data)"
    }

}
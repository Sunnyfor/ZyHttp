package com.sunny.zy.http.callback

import com.sunny.zy.http.bean.HttpResultBean
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc 网络请求回调
 * Author ZY
 * Date 2022/4/26
 */
abstract class HttpResult<T> {
    lateinit var type: Type

    init {
        javaClass.genericSuperclass?.let {
            if (it is ParameterizedType) {
                type = it.actualTypeArguments[0]
            }
        }
    }

    abstract fun onResult(httpResultBean: HttpResultBean<T>)
}
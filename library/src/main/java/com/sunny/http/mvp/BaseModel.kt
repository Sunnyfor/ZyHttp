package com.sunny.http.mvp

import com.sunny.http.ZyHttpJava

/**
 * Desc Model基础类, 供Java数据Model使用
 * Author ZY
 * Date 2022/4/29
 */
open class BaseModel<T : BasePresenter<V>, V : IBaseView>(private val presenter: T) {

    val zyHttpJava by lazy { ZyHttpJava(presenter) }

    fun getPresenter(): T {
        return presenter
    }
}
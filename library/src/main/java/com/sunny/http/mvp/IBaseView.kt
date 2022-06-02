package com.sunny.http.mvp

/**
 * Desc 接口 IView基类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/8/2
 */
interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun showMessage(message: String)
}
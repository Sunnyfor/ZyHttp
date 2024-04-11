package com.sunny.http.utils

import com.sunny.kit.utils.application.ZyKit
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Desc Cookie持久化存储
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/6/12
 */
class ZyCookieJar : CookieJar {

    private val cookieStore = HashMap<String, List<Cookie>>()

    private val fileName = "ZyCookie"

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
        ZyKit.sp(fileName).set(url.host, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        var list = cookieStore[url.host]
        if (list == null) {
            list = ZyKit.sp(fileName).getList<Cookie>(url.host)
                ?: arrayListOf()
        }
        cookieStore[url.host] = list
        return list
    }

    /**
     * 清除Cookie
     */
    fun clearCookie() {
        cookieStore.clear()
        ZyKit.sp(fileName).clear()
    }
}
package com.sunny.http.utils

import com.google.gson.reflect.TypeToken
import com.sunny.kit.utils.SpUtil
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
        SpUtil.get(fileName).setObject(url.host, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        var list = cookieStore[url.host]
        if (list == null) {
            list = SpUtil.get(fileName).getObject(url.host, object : TypeToken<List<Cookie>>() {}.type)
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
        SpUtil.get(fileName).clear()
    }
}
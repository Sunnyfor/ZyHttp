package com.sunny.zy.http.bean

import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Desc Socket请求结果实体类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/4/13
 */
abstract class WebSocketResultBean : WebSocketListener() {
    var webSocket: WebSocket? = null
}
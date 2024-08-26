package com.sunny.http

import com.sunny.http.bean.BaseHttpResultBean
import com.sunny.http.interceptor.DefaultHeaderInterceptor
import com.sunny.http.interceptor.DefaultLogInterceptor
import com.sunny.http.parser.DefaultResponseParser
import com.sunny.http.parser.IResponseParser
import com.sunny.http.utils.ZyCookieJar
import okhttp3.Interceptor
import java.net.URL
import javax.net.ssl.HostnameVerifier

/**
 * Desc 网络请求配置清单
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2017/10/12
 */
object ZyHttpConfig {

    /**
     * IP地址
     */
    private var IP = "127.0.0.1" // 内网测试地址

    /**
     * 端口
     */
    private var PORT = "-1"

    /**
     * 前缀：http? https
     */
    private var PROTOCOL = "http"

    /**
     * 后缀
     */
    private var PATH = ""

    /**
     * 域名变量
     */
    var HOST: String = ""
        set(value) {
            var mValue = value.replace(" ", "")
            if (!mValue.contains("://")) {
                //默认Http协议
                mValue = "http://$value"
            }
            field = mValue
            runCatching {
                val url = URL(value)
                PORT = url.port.toString()
                IP = url.host
                PROTOCOL = url.protocol
                PATH = url.path
            }.onFailure {
                HOST = "$PROTOCOL://$value"
            }
        }

    /**
     * 获取IP
     */
    fun getIP() = IP

    /**
     * 获取端口
     */
    fun getPort() = PORT

    /**
     * 获取协议
     */
    fun getProtocol() = PROTOCOL

    /**
     * 获取路径
     */
    fun getPath() = PATH

    /**
     * 连接超时时间，单位毫秒
     */
    var CONNECT_TIME_OUT = 10 * 1000L

    /**
     * 读取超时时间，单位毫秒
     */
    var READ_TIME_OUT = 20 * 1000L


    /**
     * 写入超时时间，单位毫秒
     */
    var WRITE_TIME_OUT = 20 * 1000L


    /**
     * 设置完成调用的默认超时时间。值为0表示没有超时，否则值必须在1和Integer之间。
     */
    var CALL_TIME_OUT = 0L


    /**
     * 打印最大响应内容
     */
    var RESPONSE_BODY_MAX_LOG_SIZE = 2 * (1024 * 1024L)



    val headerInterceptor: DefaultHeaderInterceptor by lazy {
        DefaultHeaderInterceptor()
    }

    /**
     * Log拦截器
     */
    var logInterceptor: Interceptor = DefaultLogInterceptor()


    /**
     * 设置头信息
     */
    fun setHttpHeader(headerMap: HashMap<String, *>) {
        headerInterceptor.setHttpHeader(headerMap)
    }

    /**
     * 数据结果解析器
     */
    var iResponseParser: IResponseParser = DefaultResponseParser()


    /**
     * CookieJar配置
     */
    var zyCookieJar = ZyCookieJar()

    /**
     * url验证
     */
    var hostnameVerifier: HostnameVerifier = HostnameVerifier { _, _ -> true }

    /**
     * 网络请求全局回调
     */
    var httpResultCallback: ((resultBean: BaseHttpResultBean) -> Unit)? = null

}
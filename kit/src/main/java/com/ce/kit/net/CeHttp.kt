package com.ce.kit.net

import com.ce.kit.CeKit
import java.net.Inet6Address
import java.util.concurrent.TimeUnit
import okhttp3.Dns
import okhttp3.OkHttpClient

/**
 * Tries IPv4 addresses before IPv6 ones. On some networks the phone has an IPv6 address and a route that does not
 * really work: the connection to the IPv6 address fails or waits until the timeout, while the IPv4 one would work at once.
 */
object Ipv4First : Dns {
    override fun lookup(hostname: String): List<java.net.InetAddress> = Dns.SYSTEM.lookup(hostname).sortedBy { it is Inet6Address }
}

/**
 * The one HTTP client of an app (one connection pool and one set of timeouts). Calls that need something else
 * (a longer read timeout, say) derive theirs from it with [OkHttpClient.newBuilder], which keeps sharing the pool.
 * Every request gets a `User-Agent` of the form `<App>/<version> (Android)` unless it sets its own.
 */
object CeHttp {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(Ipv4First)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.header("User-Agent") != null) {
                    chain.proceed(request)
                } else {
                    chain.proceed(request.newBuilder().header("User-Agent", userAgent()).build())
                }
            }
            .build()
    }

    fun userAgent(): String = "${CeKit.config.appName}/${CeKit.config.versionName} (Android)"
}

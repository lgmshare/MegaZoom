package com.zcba.megazoom.app

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.zcba.megazoom.BuildConfig
import com.zcba.megazoom.utils.SharePrefUtils
import com.zcba.megazoom.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object CloakHelper {

    private const val CLOAK_URL = "https://stefan.megazoom.net/bessel/arbiter"

    fun request() {
        if (SharePrefUtils.cloakLoadedEnable) {
            Utils.log("已获取当前用户的状态")
            return
        }
        MainScope().launch {
            launch(Dispatchers.IO) {
                var count = 3
                while (isActive && count > 0) {
                    val result = loadCloakParams()
                    if (result) {
                        break
                    }
                    count--
                }
            }
        }
    }

    private fun loadCloakParams(): Boolean {
        val obj = JSONObject().apply {
            put("oneill", getAndroidID())
            put("iron", System.currentTimeMillis())
            put("aver", Build.MODEL)
            put("wizard", BuildConfig.APPLICATION_ID)
            put("sheave", Build.VERSION.RELEASE)
            put("tanzania", "")
            put("monkey", getGoogleAdId())
            put("shouldnt", getAndroidID())
            put("musty", "android")
            put("cord", "")
            put("clone", BuildConfig.VERSION_NAME)
            put("dry", "22.5.0")
            put("curd", getOperateId())
        }

        val sb = StringBuilder()
        obj.keys().forEach {
            val value = URLEncoder.encode(obj.optString(it))
            if (!value.isNullOrBlank()) {
                if (sb.isEmpty()) {
                    sb.append("$it=${value}")
                } else {
                    sb.append("&$it=${value}")
                }
            }
        }
        val requestUrl = "${CLOAK_URL}?${sb.toString()}"
        val client = OkHttpClient().newBuilder()
            .connectTimeout(17, TimeUnit.SECONDS)
            .readTimeout(17, TimeUnit.SECONDS)
            .writeTimeout(17, TimeUnit.SECONDS)
            .build()
        Utils.log(requestUrl)
        var responseInfo: Response? = null
        try {
            val request = Request.Builder().url(requestUrl).build()
            responseInfo = client.newCall(request).execute()
            if (responseInfo.isSuccessful) {
                val result = responseInfo.body?.string()
                Utils.log(result)
                SharePrefUtils.cloakEnable = result == "family"
                SharePrefUtils.cloakLoadedEnable = true
                return true
            } else {
                Utils.log("请求失败")
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            responseInfo?.close()
        }
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidID(): String {
        val id = Settings.Secure.getString(
            App.INSTANCE.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
        return if ("9774d56d682e549c" == id) "" else id
    }

    private fun getGoogleAdId(): String? {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(App.INSTANCE).id
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun getOperateId(): String {
        val operator = App.INSTANCE.getSystemService(TelephonyManager::class.java).networkOperator
        /**通过operator获取 MCC 和MNC */
        return runCatching {
            val mcc = operator.substring(0, 3).toInt()
            val mnc = operator.substring(3).toInt()
            "$mcc$mnc"
        }.getOrDefault("")
    }
}
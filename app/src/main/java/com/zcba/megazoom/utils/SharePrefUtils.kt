package com.zcba.megazoom.utils

import android.content.Context
import com.zcba.megazoom.app.App

object SharePrefUtils {
    private val sharePref = App.INSTANCE.getSharedPreferences("mega_zoom", Context.MODE_PRIVATE)

    var cloakEnable: Boolean
        get() = sharePref.getBoolean("cloakEnable", true)
        set(value) = sharePref.edit().putBoolean("cloakEnable", value).apply()

    var cloakLoadedEnable: Boolean
        get() = sharePref.getBoolean("cloakLoadedEnable", false)
        set(value) = sharePref.edit().putBoolean("cloakLoadedEnable", value).apply()


}
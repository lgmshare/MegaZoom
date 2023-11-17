package com.zcba.megazoom.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.zcba.megazoom.ui.WelcomeActivity
import java.lang.ref.WeakReference

class App : Application() {

    private var activityWeakReference: WeakReference<Activity>? = null
    var isAppForeground = false
    var activityCount = 0

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        init()
    }

    private fun init() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                val activity = activityWeakReference?.get()
                if (activity != null && activity !is WelcomeActivity) {
                    activity.startActivity(Intent(activity, WelcomeActivity::class.java))
                }
                isAppForeground = true
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                isAppForeground = false
            }
        })

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                activityCount++
            }

            override fun onActivityStarted(p0: Activity) {
                activityWeakReference = WeakReference(p0)
            }

            override fun onActivityResumed(p0: Activity) {
            }

            override fun onActivityPaused(p0: Activity) {
            }

            override fun onActivityStopped(p0: Activity) {

            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(p0: Activity) {
                activityCount--
                val activity = activityWeakReference?.get()
                if (activity == p0) {
                    activityWeakReference = null
                }
            }
        })
    }
}
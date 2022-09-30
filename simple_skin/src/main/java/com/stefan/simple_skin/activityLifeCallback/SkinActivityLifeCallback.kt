package com.stefan.simple_skin.activityLifeCallback

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.ArrayMap
import androidx.core.view.LayoutInflaterCompat
import com.stefan.simple_skin.SimpleSkin
import com.stefan.simple_skin.factory.SkinLayoutInflaterFactory

internal class SkinActivityLifeCallback : Application.ActivityLifecycleCallbacks {

    private val activityFactory2Map = ArrayMap<String, SkinLayoutInflaterFactory>()

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityPreCreated(activity, savedInstanceState)
        val factory2 = SkinLayoutInflaterFactory(activity)
        LayoutInflaterCompat.setFactory2(activity.layoutInflater, factory2)
        activityFactory2Map[activity.javaClass.canonicalName] = factory2
        SimpleSkin.mInnerObservable.addObserver(factory2)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityFactory2Map[activity.javaClass.canonicalName]?.clearInvalidViews()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        val factory2: SkinLayoutInflaterFactory? = activityFactory2Map.remove(activity.javaClass.canonicalName)
        factory2?.destroy()
        SimpleSkin.mInnerObservable.deleteObserver(factory2)
        clearInvalidFactories()
    }

    private fun clearInvalidFactories() {
        val iterator = activityFactory2Map.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.value.isFactoryInvalid()) {
                item.value.destroy()
                SimpleSkin.mInnerObservable.deleteObserver(item.value)
                iterator.remove()
            }
        }
    }
}
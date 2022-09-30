package com.stefan.simple_skin.factory

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.stefan.simple_skin.SimpleSkin
import com.stefan.simple_skin.cache.ViewAttributeCache
import com.stefan.simple_skin.watcher.SkinChangeWatcher
import java.lang.ref.WeakReference
import java.lang.reflect.Constructor
import java.util.*

internal class SkinLayoutInflaterFactory(activity: Activity) : LayoutInflater.Factory2, Observer {

    private val sConstructorMap = HashMap<String, Constructor<out View>>()

    private val mClassPrefixList = arrayOf(
        "android.widget.",
        "android.webkit.",
        "android.app.",
        "android.view."
    )

    private val cache: ViewAttributeCache = ViewAttributeCache()

    private var weakReference: WeakReference<Activity>

    init {
        SimpleSkin.mInnerObservable.addObserver(this)
        weakReference =  WeakReference<Activity>(activity)
    }

    private fun getContent() = weakReference.get()

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        var view = tryCreateView(name, context, attrs)
        if (null == view) {
            //自定义View 或者 类似 ConstraintLayout 这种带全限定名的会走这里
            view = createView(name, context, attrs)
        }
        if (view != null){
            cache.checkAndCache(view, attrs)?.applySkin(context)
        }
        return view
    }

    private fun tryCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if (-1 != name.indexOf('.')) {
            return null
        }
        for (i in mClassPrefixList.indices) {
            val view = createView(mClassPrefixList[i] + name, context, attrs)
            if (view != null) {
                return view
            }
        }
        return null
    }

    /**
     * 通过反射创建view 参考系统源码
     */
    private fun createView(name: String, context: Context, attrs: AttributeSet): View? {
        val constructor: Constructor<out View>? = findConstructor(context, name)
        try {
            return constructor?.newInstance(context, attrs)
        } catch (e: Exception) {
        }
        return null
    }

    private fun findConstructor(context: Context, name: String): Constructor<out View>? {
        var constructor: Constructor<out View>? = sConstructorMap[name]
        if (constructor == null) {
            try {
                val clazz = context.classLoader.loadClass(name).asSubclass(View::class.java)
                constructor =
                    clazz.getConstructor(Context::class.java, AttributeSet::class.java)
                sConstructorMap[name] = constructor
            } catch (e: Exception) {
            }
        }
        return constructor
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return null
    }

    override fun update(o: Observable?, arg: Any?) {
        val context = getContent()?: return
        cache.applySkin(context)
        if (context is SkinChangeWatcher) {
            (context as? SkinChangeWatcher)?.onSkinChange()
        }
        if (context is FragmentActivity) {
            val pendingList = LinkedList<Fragment>()
            val activityFragments = (context as? FragmentActivity)?.supportFragmentManager?.fragments ?: emptyList()
            pendingList.addAll(activityFragments)
            while (pendingList.isNotEmpty()) {
                val fragment = pendingList.poll()
                if (fragment != null) {
                    if (fragment is SkinChangeWatcher) {
                        (fragment as? SkinChangeWatcher)?.onSkinChange()
                    }
                    if (fragment.host != null) {
                        val childFragments = fragment.childFragmentManager.fragments
                        pendingList.addAll(childFragments)
                    }
                }
            }
        }
    }

    fun isFactoryInvalid() = weakReference.get() == null

    fun clearInvalidViews() {
        cache.clearInvalidViews()
    }

    fun destroy(){
        sConstructorMap.clear()
        cache.clear()
        weakReference.clear()
    }
}
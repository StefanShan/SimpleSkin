package com.stefan.simple_skin.cache

import android.app.Activity
import android.util.AttributeSet
import android.view.View
import com.stefan.simple_skin.getThemeResId
import com.stefan.simple_skin.watcher.SkinChangeWatcher

internal data class AttrPair(val attributeName: String, val resId: Int)

internal class ViewAttributeCache {
    private val mSupportAttributes: MutableList<String> = mutableListOf(
        "background",
        "src",
        "textColor",
        "textColorHint",
        "drawableLeft",
        "drawableTop",
        "drawableRight",
        "drawableBottom",
        "cardBackgroundColor",
    )

    private val mViewAttrList = mutableListOf<ViewAttr>()

    //缓存View中的attribute属性
    fun checkAndCache(view: View, attrs: AttributeSet): ViewAttr?{
        val skinPairs: MutableList<AttrPair> = ArrayList()
        for (index in 0 until attrs.attributeCount){
            val attributeName = attrs.getAttributeName(index)
            val attributeValue = attrs.getAttributeValue(index)
            if (mSupportAttributes.contains(attributeName)){
                /**
                 * 颜色值类型
                 *
                 * #123112 写死的颜色值
                 * @112323 等于 @color/pink
                 * ?2130903265 等于 ?attr/colorError
                 */
                //写死的颜色值，不处理
                if (attributeValue.startsWith("#")) continue
                val resId = if (attributeValue.startsWith("?")) {
                    val attrId = attributeValue.substring(1).toInt()
                    getThemeResId(view.context, intArrayOf(attrId))[0]
                } else {
                    attributeValue.substring(1).toInt()
                }
                skinPairs.add(AttrPair(attributeName, resId))
            }
        }
        return if (skinPairs.isNotEmpty() || view is SkinChangeWatcher){
            ViewAttr(view,skinPairs).apply{
                mViewAttrList.add(this)
            }
        }else null
    }

    /**
     * 对所有的view中的所有的属性进行皮肤修改
     */
    fun applySkin(activity: Activity) {
        for (viewAttr in mViewAttrList) {
            viewAttr.applySkin(activity)
        }
    }

    fun clear(){
        mViewAttrList.clear()
    }

    fun clearInvalidViews() {
        val iterator = mViewAttrList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.isViewInvalid()) {
                iterator.remove()
            }
        }
    }
}
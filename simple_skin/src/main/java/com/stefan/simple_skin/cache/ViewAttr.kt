package com.stefan.simple_skin.cache

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import com.stefan.simple_skin.getBackground
import com.stefan.simple_skin.getColorStateList
import com.stefan.simple_skin.getDrawable
import com.stefan.simple_skin.watcher.SkinChangeWatcher
import java.lang.ref.WeakReference

internal class ViewAttr private constructor() {

    lateinit var mRealViewRef: WeakReference<View>
    var mAttrPairs: List<AttrPair>? = null

    constructor(view:View, attrPairs: MutableList<AttrPair>) : this() {
        mRealViewRef = WeakReference(view)
        mAttrPairs = attrPairs
    }

    /**
     * 对当前view执行换肤操作
     */
    fun applySkin(context: Context) {
        mAttrPairs ?: return
        val view = mRealViewRef?.get() ?: return
        //如果当前view是自定义view 则 执行其自己的除了常规的background、textColor等属性的换肤逻辑
        for ((attributeName, resId) in mAttrPairs!!) {
            var left: Drawable? = null
            var top: Drawable? = null
            var right: Drawable? = null
            var bottom: Drawable? = null
            if (resId <= 0) {
                continue
            }
            when (attributeName) {
                "background" -> {
                    val background = getBackground(context, resId)
                    //背景可能是 @color 也可能是 @drawable
                    if (background is Int) {
                        view.setBackgroundColor(background)
                    } else {
                        ViewCompat.setBackground(view, background as? Drawable)
                    }
                }
                "src" -> {
                    val background = getBackground(context, resId)
                    if (background is Int) {
                        (view as ImageView).setImageDrawable(ColorDrawable(background))
                    } else {
                        (view as ImageView).setImageDrawable(background as? Drawable)
                    }
                }
                "textColor" -> {
                    val colorStateList = getColorStateList(context, resId)
                    if (colorStateList != null) {
                        (view as? TextView)?.setTextColor(colorStateList)
                    }
                }
                "textColorHint" -> {
                    val colorStateList = getColorStateList(context, resId)
                    if (colorStateList != null) {
                        (view as? TextView)?.setHintTextColor(colorStateList)
                    }
                }
                "drawableLeft" -> left = getDrawable(context, resId)
                "drawableTop" -> top = getDrawable(context, resId)
                "drawableRight" -> right = getDrawable(context, resId)
                "drawableBottom" -> bottom = getDrawable(context, resId)
                "cardBackgroundColor" -> {
                    val cardBackground = getColorStateList(context, resId)
                    if (cardBackground != null) {
                        (view as? CardView)?.setCardBackgroundColor(cardBackground)
                    }
                }
                else -> {
                }
            }

            if (null != left || null != right || null != top || null != bottom) {
                (view as? TextView)?.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
            }
        }
        if (view is SkinChangeWatcher) {
            (view as SkinChangeWatcher).onSkinChange()
        }
    }

    fun isViewInvalid() = mRealViewRef.get() == null
}
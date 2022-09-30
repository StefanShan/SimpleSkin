package com.stefan.simple_skin

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * 获得theme中的属性中定义的 资源id
 * @param context
 * @param attrs
 * @return
 */
internal fun getThemeResId(context: Context, attrs: IntArray): IntArray {
    val resIds = IntArray(attrs.size)
    val a: TypedArray = context.obtainStyledAttributes(attrs)
    for (i in attrs.indices) {
        resIds[i] = a.getResourceId(i, 0)
    }
    a.recycle()
    return resIds
}

/**
 * 可能是Color 也可能是drawable
 *
 * @return
 */
internal fun getBackground(context: Context, resId: Int): Any? {
    val resourceTypeName = context.resources.getResourceTypeName(resId)
    return if ("color" == resourceTypeName) {
        getColor(context, resId)
    } else {
        // drawable
        getDrawable(context, resId)
    }
}

internal fun getColor(context: Context, resId: Int): Int {
    return ResourcesCompat.getColor(context.resources, resId, null)
}

internal fun getColorStateList(context: Context, resId: Int): ColorStateList? {
    return ResourcesCompat.getColorStateList(context.resources, resId, null)
}

internal fun getDrawable(context: Context, resId: Int): Drawable? {
    return ResourcesCompat.getDrawable(context.resources, resId, null)
}
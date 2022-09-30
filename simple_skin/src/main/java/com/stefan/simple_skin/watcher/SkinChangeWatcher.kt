package com.stefan.simple_skin.watcher

import androidx.annotation.Keep

/**
 * 自定义view 需要实现此接口 已实现自身的换肤功能
 */
@Keep
interface SkinChangeWatcher {
    fun onSkinChange()
}
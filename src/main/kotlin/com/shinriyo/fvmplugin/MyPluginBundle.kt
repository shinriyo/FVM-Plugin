package com.shinriyo.fvmplugin

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "messages.MyPluginBundle"

object MyPluginBundle : DynamicBundle(BUNDLE) {

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return getMessage(key, *params)
    }
}

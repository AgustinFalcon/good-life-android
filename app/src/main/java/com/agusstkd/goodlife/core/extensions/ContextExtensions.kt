package com.agusstkd.goodlife.core.extensions

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

/**
 * Recorre la cadena de Context wrappers hasta encontrar la FragmentActivity.
 *
 * Útil para obtener la Activity desde un Composable via LocalContext.current.
 * Retorna null si no se encuentra (ej: en @Preview).
 */
fun Context.findFragmentActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

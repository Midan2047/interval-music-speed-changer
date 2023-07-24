package com.ddodang.intervalmusicspeedchanger.local.util

import androidx.core.text.HtmlCompat
import com.ddodang.intervalmusicspeedchanger.data.util.HtmlEscapeUtil
import javax.inject.Inject

internal class HtmlEscapeUtilImpl @Inject constructor() : HtmlEscapeUtil {

    override fun fromHtml(htmlString: String): String = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()

}
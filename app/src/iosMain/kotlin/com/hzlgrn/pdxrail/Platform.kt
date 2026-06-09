package com.hzlgrn.pdxrail

import platform.Foundation.NSBundle

actual val platformVersionName: String
    get() = NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleShortVersionString") as? String ?: "unknown"

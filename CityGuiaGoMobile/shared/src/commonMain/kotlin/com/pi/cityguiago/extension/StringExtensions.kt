package com.pi.cityguiago.extension

fun fixUrl(url: String): String {
    return when {
        url.startsWith("https://") -> url
        url.startsWith("http://") -> url.replaceFirst("http://", "https://")
        url.isNotBlank() -> "https://$url"
        else -> url // or return a default URL if you prefer
    }
}
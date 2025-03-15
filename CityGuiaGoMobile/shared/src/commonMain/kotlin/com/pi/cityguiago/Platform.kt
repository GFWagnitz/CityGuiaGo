package com.pi.cityguiago

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
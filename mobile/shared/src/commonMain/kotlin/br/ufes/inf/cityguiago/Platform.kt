package br.ufes.inf.cityguiago

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
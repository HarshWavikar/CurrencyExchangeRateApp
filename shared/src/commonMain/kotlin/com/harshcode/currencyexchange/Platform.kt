package com.harshcode.currencyexchange

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
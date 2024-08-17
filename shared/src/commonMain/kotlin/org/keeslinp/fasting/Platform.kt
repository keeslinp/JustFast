package org.keeslinp.fasting

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
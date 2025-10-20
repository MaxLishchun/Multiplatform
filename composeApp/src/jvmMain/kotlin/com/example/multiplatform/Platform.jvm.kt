package com.example.multiplatform

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun PlatformView() {
    Text("This is MacOS")
}

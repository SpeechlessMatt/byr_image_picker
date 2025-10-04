package com.example.byr_image_picker

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.concurrent.atomic.AtomicLong

// 原子计数器，协程/线程并发安全
private val counter = AtomicLong(0)

// 这里获取临时文件对象
fun getFileFromUri(context: Context, uri: Uri): File? {
    val ctr = context.contentResolver
    return try {
        val inputStream = ctr.openInputStream(uri) ?: return null

        // 先拿 mimeType
        val mime = ctr.getType(uri) ?: "application/octet-stream"

        // 拿到扩展名
        val ext = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mime)
            ?.lowercase()
            ?: uri.path?.substringAfterLast('.')?.takeIf { it.isNotEmpty() }
            ?: "tmp" // 没有扩展名就tmp咯

        // 放缓存里面
        val cacheDir = context.cacheDir
        val tempFile = File(cacheDir, "shared_${System.nanoTime()}_${counter.getAndIncrement()}.$ext")
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        Log.e("FileUtils", "Error converting URI to File: ${e.message}")
        null
    }
}

// 小小封装一下，保证对外形式统一，以后我自己写项目也可能用的上
fun getFilePathFromUri(context: Context, uri: Uri): String? {
    return getFileFromUri(context, uri)?.absolutePath.toString()
}

// 这里获取临时文件的绝对地址列表: 协程加速！！！
suspend fun getFilePathsFromUris(context: Context, uris: List<Uri>): List<String> =
    coroutineScope {
        uris.map { uri ->
            async { getFileFromUri(context, uri) } // 转换成listOf(async{}, async{}...)
        }.awaitAll() // 等待所有子协程
            .mapNotNull { it?.absolutePath.toString() } // 过滤失败项
    }
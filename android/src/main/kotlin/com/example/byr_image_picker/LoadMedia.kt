package com.example.byr_image_picker

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

// 这个是曾经的弃用方案
// 不用这个方法了，太慢了加载的，一个一个串行加载不如调个库实在
//    // 异步加载缩略图
//    LaunchedEffect(photosUri) {
//        photosUri?.forEach { uri ->
//            Log.d("AlbumNamesList", uri.toString())
//            val bmp = withContext(Dispatchers.IO) {
//                context.contentResolver.loadThumbnail(uri, android.util.Size(200, 200), null)
//            }
//            thumbs.add(bmp)
//        }
//    }

// 这个是利用安卓MediaStore获取图片列表的方法
fun getAlbums(context: Context): Map<String, MutableList<Uri>> {
    // 定义筛选的列
    val projection = arrayOf(
        // 拿到_ID就是拿到uri了
        MediaStore.Images.Media._ID,
        // 拿到类别的名字
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    // 我在真机调试发现会闪退找到原因是照片大小为0，幽灵照片！！所以直接筛掉！
    val selection = "${MediaStore.Images.Media.SIZE} > 0"

    val cursor = context.contentResolver.query(
        // external_content_uri 是一个table
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
    )

    // 专辑名字 专辑内所有照片Uri
    val map = mutableMapOf<String, MutableList<Uri>>()

    // use的作用是自动close cursor，这是ai教的，ai真厉害
    cursor?.use {
        val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val bucketCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        // 分类
        while (it.moveToNext()) {
            val id = it.getLong(idCol)
            val album = it.getString(bucketCol) ?: "Unknown"
            // 处理一下uri,拿到完整uri
            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
            )
            // 是否有album这个键？有的话找到这个键对应的值然后.add(uri)
            // 没有就新建一个mutableListOf()，这个代码我问ai的，这个确实够简洁的
            map.getOrPut(album) {
                mutableListOf()
            }.add(uri)
        }
    }

    return map
}
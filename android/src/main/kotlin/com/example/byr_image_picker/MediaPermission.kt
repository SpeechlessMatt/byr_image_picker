package com.example.byr_image_picker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


// 写成remember可以放compose组件里面复用，肥肠好用啊！
// 相当于是把rememberLauncherForActivityResult细化封装了
@Composable
fun rememberPhotoPermissionLauncher(
    onGetAllPhotoPermission: () -> Unit = {},
    onGetPartialPhotoPermission: () -> Unit = {},
    onDeniedPermission: () -> Unit = {}
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {

    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedMap ->
        // 最低版本安卓都支持,直接用val
        val readStorage = grantedMap[READ_EXTERNAL_STORAGE] == true

        var readImages = false
        var readSelectedImages = false

        // 不这样写的话，studio黄线提示看得我强迫症犯了
        // 而且这样也避免了空索引问题吧
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            readSelectedImages = grantedMap[READ_MEDIA_VISUAL_USER_SELECTED] == true
            readImages = grantedMap[READ_MEDIA_IMAGES] == true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readImages = grantedMap[READ_MEDIA_IMAGES] == true
        }

        // when真的很好用好看
        when {
            readStorage || readImages -> onGetAllPhotoPermission()
            readSelectedImages -> onGetPartialPhotoPermission()
            else -> {
                onDeniedPermission()
            }
        }
    }
}

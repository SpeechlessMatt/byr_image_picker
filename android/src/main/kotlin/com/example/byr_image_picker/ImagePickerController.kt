package com.example.byr_image_picker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.byr_image_picker.ui.ImagePickerView

// 这是一个控制器，用来决定使用原生picker还是自带的picker
@Composable
fun ImagePickerController(
    modifier: Modifier = Modifier,
    isMultiSelected: Boolean = false,
    onResultListUri: (List<Uri>) -> Unit,
    onResultNull: () -> Unit,
) {
    val ctx = LocalContext.current

    Log.d("compose", "success load controller")

    // 系统自带picker的launcher
    val pickSinglePhotoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia())
        { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                // 全部转换成string返回
                onResultListUri(listOf(uri))
            } else {
                Log.d("PhotoPicker", "No media selected")
                onResultNull()
            }
        }

    Log.d("compose", "success load pickSinglePhotoLauncher")

    // 系统自带picker的Muti-launcher
    val pickMultiPhotoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia())
        { uris ->
            if (uris.isEmpty()) {
                Log.d("PhotoPicker", "No media selected")
                onResultNull()
            } else {
                Log.d("PhotoPicker", "Selected URI: $uris")
                onResultListUri(uris)
            }
        }

    // 权限申请表
    val permissionsArray = when {

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
            arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)

        // 好像这么写和上面都没区别。。算了写都写了就不删了，不影响的
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            arrayOf(READ_MEDIA_IMAGES)

        else ->
            arrayOf(READ_EXTERNAL_STORAGE)
    }

    var shouldShowImagePicker by remember { mutableStateOf(false) }
    var isPartialPermission by remember { mutableStateOf(false) }
    // 初始化albumNames

    val albumsMap = remember { mutableStateOf<Map<String, List<Uri>>>(emptyMap()) }

    if (shouldShowImagePicker) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable(
                    // 关掉点击动画
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        shouldShowImagePicker = false
                        onResultNull()
                    }
                )
        )
    }

    AnimatedVisibility(
        visible = shouldShowImagePicker,
        enter = slideInVertically { fullHeight -> fullHeight }   // 从 fullHeight → 0
                + fadeIn(initialAlpha = 0.3f),                   // 淡入
        exit = slideOutVertically { fullHeight -> fullHeight }  // 从 0 → fullHeight
                + fadeOut()
    ) {
        // 这里不用viewmodel这么复杂，
        // 所以业务逻辑和ui也不用抽离得太彻底，
        // 我就把albumsMap直接传进去吧，不想花太多时间了
        ImagePickerView(
            modifier = modifier,
            isPartialPermission = isPartialPermission,
            albumsMap = albumsMap.value,
            onClose = {
                shouldShowImagePicker = false
                onResultNull()
            },
            onCloseWithUri = {
                shouldShowImagePicker = false
                onResultListUri(listOf(it))
            },
        )
    }

    // 权限申请launcher，这是一个控制器
    // 至于为什么不用registerForActivityResult是因为flutterFragmentActivity不继承appCompatActivity
    // 如果能用，再说吧，毕竟我都写了已经
    val permissionLauncher = rememberPhotoPermissionLauncher(

        onGetAllPhotoPermission = {
            Log.d("PhotoPermission", "get all photo permission")
            isPartialPermission = false
            albumsMap.value = getAlbums(ctx)
            // 可以显示了：自定义imagePicker
            shouldShowImagePicker = true
        },

        // 因为用户点击就会选择部分图片导致pick两遍
        // 所以会在底下弹出提示
        onGetPartialPhotoPermission = {
            Log.d("PhotoPermission", "get partial photo permission")
            isPartialPermission = true
            albumsMap.value = getAlbums(ctx)
            // 可以显示了：自定义imagePicker
            shouldShowImagePicker = true
        },

        // 用户拒绝权限？那直接用系统的picker就好
        onDeniedPermission = {
            Log.d("PhotoPermission", "photo permission denied")
            if (isMultiSelected) {
                pickMultiPhotoLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                pickSinglePhotoLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }

        }

    )

    // 在第一次重组后launch，
    // 防止launcher还没有初始化你就launch了导致：Launcher has not been initialized
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissionsArray)
    }
}
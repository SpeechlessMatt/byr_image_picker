package com.example.byr_image_picker

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** ByrImagePickerPlugin
 * 一个超级好玩的方法调用MethodChannel，因为我发现他可以拿到flutter的activity，那我弹出个弹窗不是轻轻松松吗
 * */
class ByrImagePickerPlugin :
    FlutterPlugin,
    MethodCallHandler,
    ActivityAware {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var composeView: ComposeView? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "byr_image_picker")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
        when (call.method) {

            "getSelectedPhotoPath" -> {
                Log.d("MethodCall", "call (getSelectedPhotoPath)")
                getSelectedPhotoPath(result)
            }

            "getSelectedPhotoPaths" -> {
                Log.d("MethodCall", "call (getSelectedPhotoPaths)")
                val maxSelection: Int = call.argument<Int>("maxSelection") ?: 10
                getSelectedPhotoPaths(result, maxSelection)
            }

            else -> result.notImplemented()
        }
    }

    private fun getSelectedPhotoPath(result: Result) {
        // 拿到activity
        val act: ComponentActivity = (activity ?: return) as ComponentActivity
        // 拉起compose view获得uri
        getSelectedUri { uri ->
            val path = getFilePathFromUri(act, uri)
            closeImagePicker()
            Log.d("Path", "selected: ${path.toString()}")
            result.success(path)
        }
    }

    private fun getSelectedPhotoPaths(result: Result, maxSelection: Int) {
        // 拿到activity
        val act: ComponentActivity = (activity ?: return) as ComponentActivity
        // 拉起compose view获得uris
        getSelectedUris(maxSelection) { uris ->
            // 利用协程批量将文件放到temp然后发送绝对地址给flutter
            act.lifecycleScope.launch {
                Log.d("ktx", "start: $uris")
                val paths = withContext(Dispatchers.IO) {
                    // 协程：挂起函数：getFilePathsFromUris()
                    getFilePathsFromUris(act, uris)
                }
                closeImagePicker()
                Log.d("ktx", "finish: $paths")
                result.success(paths)
            }
        }
    }

    private fun getSelectedUri(
        callbackWithSelectedUri: (Uri) -> Unit
    ) {
        val act: ComponentActivity = (activity ?: return) as ComponentActivity
        if (composeView != null) return

        // 注入compose view
        composeView = ComposeView(act).apply {
            setViewTreeSavedStateRegistryOwner(act)
            setViewTreeLifecycleOwner(act)
            setContent {
                // 劫持返回键返回到flutter而不是返回到桌面
                BackHandler {
                    closeImagePicker()
                }
                // 好，这个时候可能会觉得很怪，为啥这里不分开写成两种
                // 一个多选一个不多选
                // 因为这里面含有权限管理和拒绝后多选使用系统picker的选项
                ImagePickerController(
                    modifier = Modifier.fillMaxSize(),
                    isMultiSelected = false,
                    onResultListUri = {
                        callbackWithSelectedUri(it.first())
                    },
                    onResultNull = {
                        closeImagePicker()
                    },
                )
            }
        }

        Log.d("compose", "activity = $activity, composeView = $composeView")

        act.addContentView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    // 其实和上面那个几乎一模一样，别问我为啥要分开写，
    // 为了以后维护简单些吧
    // 获得uri列表
    private fun getSelectedUris(
        maxSelection: Int,
        callbackWithSelectedUris: (List<Uri>) -> Unit
    ) {
        val act: ComponentActivity = (activity ?: return) as ComponentActivity
        if (composeView != null) return

        // 注入compose view
        composeView = ComposeView(act).apply {
            setViewTreeSavedStateRegistryOwner(act)
            setViewTreeLifecycleOwner(act)
            setContent {
                // 劫持返回键返回到flutter而不是返回到桌面
                BackHandler {
                    closeImagePicker()
                }
                // 好，这个时候可能会觉得很怪，为啥这里不分开写成两种
                // 一个多选一个不多选
                // 因为这里面含有权限管理和拒绝后多选使用系统picker的选项
                ImagePickerController(
                    modifier = Modifier.fillMaxSize(),
                    isMultiSelected = true,
                    maxSelection = maxSelection,
                    onResultListUri = {
                        callbackWithSelectedUris(it)
                    },
                    onResultNull = { closeImagePicker() },
                )
            }
        }

        Log.d("compose", "activity = $activity, composeView = $composeView")

        act.addContentView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun closeImagePicker() {
        composeView?.let {
            val parent = it.parent as? ViewGroup
            parent?.removeView(it)
            // 直接销毁composeView
            it.disposeComposition()
            composeView = null
        }
        Log.d("compose", "success dispose compose")
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        // 拿到flutterFragmentActivity
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        // 拿到flutterFragmentActivity
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

}
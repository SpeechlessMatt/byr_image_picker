package com.example.byr_image_picker

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

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

            "getSelectedUri" -> {
                getSelectedUri(result)
            }

            "getSelectedUris" -> {
                // 多选先不做了，我还有事情
                result.notImplemented()
            }

            else -> result.notImplemented()
        }
    }

    private fun getSelectedUri(result: Result) {
        val act: ComponentActivity = (activity ?: return) as ComponentActivity
        if (composeView != null) return

        composeView = ComposeView(act).apply {
            setViewTreeSavedStateRegistryOwner(act)
            setViewTreeLifecycleOwner(act)
            setContent {
                ImagePickerController(
                    modifier = Modifier.fillMaxSize(),
                    isMultiSelected = false,
                    onResultListString = {
                        closeImagePicker()
                        result.success(it.first())
                    },
                    onResultNull = {
                        closeImagePicker()
                        result.notImplemented()
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

    private fun getSelectedUris(result: Result) {
        val act: ComponentActivity = (activity ?: return) as ComponentActivity
        if (composeView != null) return

//        val owner = act as? androidx.activity.ComponentActivity ?: return

        composeView = ComposeView(act).apply {
            setViewTreeSavedStateRegistryOwner(act)
            setViewTreeLifecycleOwner(act)
            setContent {
                ImagePickerController(
                    modifier = Modifier.fillMaxSize(),
                    isMultiSelected = false,
                    onResultListString = {
                        closeImagePicker()
                        result.success(it)
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
        // 直接销毁composeView
        composeView?.let {
            val parent = it.parent as? ViewGroup
            parent?.removeView(it)
            it.disposeComposition()
            composeView = null
        }
        Log.d("compose", "success dispose compose")
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

}
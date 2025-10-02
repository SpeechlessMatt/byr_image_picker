import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'byr_image_picker_platform_interface.dart';

/// An implementation of [ByrImagePickerPlatform] that uses method channels.
class MethodChannelByrImagePicker extends ByrImagePickerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('byr_image_picker');

  @override
  Future<String> getSelectedUri() async {
    final uriString = await methodChannel.invokeMethod("getSelectedUri");
    return uriString;
  }

  @override
  Future<List<String>> getSelectedUris() async {
    final uriStrings = await methodChannel.invokeMethod("getSelectedUri");
    return uriStrings;
  }

}

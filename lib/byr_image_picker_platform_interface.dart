import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'byr_image_picker_method_channel.dart';

abstract class ByrImagePickerPlatform extends PlatformInterface {
  /// Constructs a ByrImagePickerPlatform.
  ByrImagePickerPlatform() : super(token: _token);

  static final Object _token = Object();

  static ByrImagePickerPlatform _instance = MethodChannelByrImagePicker();

  /// The default instance of [ByrImagePickerPlatform] to use.
  ///
  /// Defaults to [MethodChannelByrImagePicker].
  static ByrImagePickerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ByrImagePickerPlatform] when
  /// they register themselves.
  static set instance(ByrImagePickerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String> getSelectedUri() {
    throw UnimplementedError("getSelectedUri() has not been implemented.");
  }

  Future<List<String>> getSelectedUris() {
    throw UnimplementedError("getSelectedUris() has not been implemented.");
  }

}


import 'byr_image_picker_platform_interface.dart';

class ByrImagePicker {
  static Future<String> getSelectedUri() {
    return ByrImagePickerPlatform.instance.getSelectedUri();
  }

  static Future<List<String>> getSelectedUris() {
    return ByrImagePickerPlatform.instance.getSelectedUris();
  }
}

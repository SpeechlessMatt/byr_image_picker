
import 'byr_image_picker_platform_interface.dart';

class ByrImagePicker {
  static Future<String?> getSelectedPhotoPath() {
    return ByrImagePickerPlatform.instance.getSelectedPhotoPath();
  }

  static Future<List<String>?> getSelectedPhotoPaths(int maxSelection) {
    return ByrImagePickerPlatform.instance.getSelectedPhotoPaths(maxSelection);
  }
}

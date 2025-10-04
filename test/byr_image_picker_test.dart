import 'package:flutter_test/flutter_test.dart';
import 'package:byr_image_picker/byr_image_picker.dart';
import 'package:byr_image_picker/byr_image_picker_platform_interface.dart';
import 'package:byr_image_picker/byr_image_picker_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockByrImagePickerPlatform
    with MockPlatformInterfaceMixin
    implements ByrImagePickerPlatform {
  @override
  Future<String?> getSelectedPhotoPath() => Future.value('/tmp/test');

  @override
  Future<List<String>?> getSelectedPhotoPaths(int maxSelection) async {
    if (maxSelection <= 0) throw ArgumentError('maxCount must > 0');
    return [
      '/tmp/test1',
      '/tmp/test',
    ];
  }
}

void main() {
  final ByrImagePickerPlatform initialPlatform =
      ByrImagePickerPlatform.instance;

  test('$MethodChannelByrImagePicker is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelByrImagePicker>());
  });

  test('getSelectedPhotoPath', () async {
    MockByrImagePickerPlatform fakePlatform = MockByrImagePickerPlatform();
    ByrImagePickerPlatform.instance = fakePlatform;

    expect(await ByrImagePicker.getSelectedPhotoPath(), '/tmp/test');
  });

  test('getSelectedPhotoPaths', () async {
    MockByrImagePickerPlatform fakePlatform = MockByrImagePickerPlatform();
    ByrImagePickerPlatform.instance = fakePlatform;

    expect(await ByrImagePicker.getSelectedPhotoPaths(10), [
      '/tmp/test1',
      '/tmp/test',
    ]);
  });
}

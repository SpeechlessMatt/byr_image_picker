// import 'package:flutter_test/flutter_test.dart';
// import 'package:byr_image_picker/byr_image_picker.dart';
// import 'package:byr_image_picker/byr_image_picker_platform_interface.dart';
// import 'package:byr_image_picker/byr_image_picker_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockByrImagePickerPlatform
//     with MockPlatformInterfaceMixin
//     implements ByrImagePickerPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final ByrImagePickerPlatform initialPlatform = ByrImagePickerPlatform.instance;

//   test('$MethodChannelByrImagePicker is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelByrImagePicker>());
//   });

//   test('getPlatformVersion', () async {
//     ByrImagePicker byrImagePickerPlugin = ByrImagePicker();
//     MockByrImagePickerPlatform fakePlatform = MockByrImagePickerPlatform();
//     ByrImagePickerPlatform.instance = fakePlatform;

//     expect(await byrImagePickerPlugin.getPlatformVersion(), '42');
//   });
// }

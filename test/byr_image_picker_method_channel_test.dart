// import 'package:flutter/services.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:byr_image_picker/byr_image_picker_method_channel.dart';

// void main() {
//   TestWidgetsFlutterBinding.ensureInitialized();

//   MethodChannelByrImagePicker platform = MethodChannelByrImagePicker();
//   const MethodChannel channel = MethodChannel('byr_image_picker');

//   setUp(() {
//     TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
//       channel,
//       (MethodCall methodCall) async {
//         return '42';
//       },
//     );
//   });

//   tearDown(() {
//     TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
//   });

//   test('getPlatformVersion', () async {
//     expect(await platform.getSelectedPhotoPath(), '42');
//   });
// }

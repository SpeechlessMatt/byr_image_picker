import 'package:flutter/material.dart';
// import 'dart:async';

import 'package:flutter/services.dart';
import 'package:byr_image_picker/byr_image_picker.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text("Flutter 调用 Compose Overlay")),
        body: Center(
          child: ElevatedButton(
            onPressed: () async {
                try {
                  // 单张
                  final uri = await ByrImagePicker.getSelectedPhotoPath();
                  print('单选URI: $uri');

                } on PlatformException catch (e) {
                  print('用户取消或出错: ${e.message}');
                }
              // ByrImagePicker.getSelectedUri();
            },
            child: const Text("显示 Compose UI"),
          ),
        ),
      ),
    );
  }
}

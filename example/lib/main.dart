import 'package:flutter/material.dart';
// import 'dart:async';

// import 'package:flutter/services.dart';
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
            onPressed: () {
              ByrImagePicker.getSelectedUri();
            },
            child: const Text("显示 Compose UI"),
          ),
        ),
      ),
    );
  }
}

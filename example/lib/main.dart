import 'dart:developer';
import 'dart:io';

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
    return MaterialApp(home: ExampleAppBody());
  }
}

class ExampleAppBody extends StatefulWidget {
  const ExampleAppBody({super.key});

  @override
  State<ExampleAppBody> createState() => _ExampleAppBody();
}

class _ExampleAppBody extends State<ExampleAppBody> {
  final List<File> files = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Byr_image_picker demo")),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          try {
            final pickedPath = await ByrImagePicker.getSelectedPhotoPaths(10);
            if (pickedPath != null) {
              setState(() => files.addAll(pickedPath.map((e) => File(e))));
            }
          } on PlatformException catch (e) {
            log("PlatformException ${e.message}");
          }
        },
        child: const Icon(Icons.add),
      ),
      body: PhotoGrid(files: files),
    );
  }
}

class PhotoGrid extends StatelessWidget {
  final List<File> files;

  const PhotoGrid({super.key, required this.files});

  @override
  Widget build(BuildContext context) {
    return GridView.builder(
      padding: EdgeInsets.all(4),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 3, // 三列
        crossAxisSpacing: 4,
        mainAxisSpacing: 4,
      ),
      itemCount: files.length,
      itemBuilder: (_, i) => Image.file(files[i], fit: BoxFit.cover),
    );
  }
}

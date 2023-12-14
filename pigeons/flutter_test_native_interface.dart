import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/src/messages.g.dart',
  dartOptions: DartOptions(),
  dartPackageName: 'flutter_test_native',
  dartTestOut: 'test/test_api.g.dart',
  javaOut: 'android/src/main/java/io/flutter/flutter_test_native/Messages.java',
  javaOptions: JavaOptions(
    package: 'io.flutter.flutter_test_native',
  ),
))

// Define the set of messages

class SelectorMessage {
  // Will find widget(s) that contains text.
  String? containsText;
  // Will find widget(s) with class name.
  String? className;
  // Will find widget with the id.
  int? id;
  // Will find the widget currently focused.
  bool? focused;
  // In the case of multiple matches, index specifies which ones.
  int? index;
}

@HostApi
abstract class FlutterTestNativeApi {
  @async
  void tap(SelectorMessage selector);
}

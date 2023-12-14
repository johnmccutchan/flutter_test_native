import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_test_native/flutter_test_native.dart';
import 'package:flutter_test_native/flutter_test_native_platform_interface.dart';
import 'package:flutter_test_native/flutter_test_native_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterTestNativePlatform
    with MockPlatformInterfaceMixin
    implements FlutterTestNativePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterTestNativePlatform initialPlatform = FlutterTestNativePlatform.instance;

  test('$MethodChannelFlutterTestNative is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterTestNative>());
  });

  test('getPlatformVersion', () async {
    FlutterTestNative flutterTestNativePlugin = FlutterTestNative();
    MockFlutterTestNativePlatform fakePlatform = MockFlutterTestNativePlatform();
    FlutterTestNativePlatform.instance = fakePlatform;

    expect(await flutterTestNativePlugin.getPlatformVersion(), '42');
  });
}

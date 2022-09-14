// File generated by FlutterFire CLI.
// ignore_for_file: lines_longer_than_80_chars, avoid_classes_with_only_static_members
import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

/// Default [FirebaseOptions] for use with your Firebase apps.
///
/// Example:
/// ```dart
/// import 'firebase_options.dart';
/// // ...
/// await Firebase.initializeApp(
///   options: DefaultFirebaseOptions.currentPlatform,
/// );
/// ```
class DefaultFirebaseOptions {
  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      return web;
    }
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return android;
      case TargetPlatform.iOS:
        return ios;
      case TargetPlatform.macOS:
        return macos;
      case TargetPlatform.windows:
        throw UnsupportedError(
          'DefaultFirebaseOptions have not been configured for windows - '
          'you can reconfigure this by running the FlutterFire CLI again.',
        );
      case TargetPlatform.linux:
        throw UnsupportedError(
          'DefaultFirebaseOptions have not been configured for linux - '
          'you can reconfigure this by running the FlutterFire CLI again.',
        );
      default:
        throw UnsupportedError(
          'DefaultFirebaseOptions are not supported for this platform.',
        );
    }
  }

  static const FirebaseOptions web = FirebaseOptions(
    apiKey: 'AIzaSyBwTWcK8NYWaQuT7JzP0REa4NuFCbdQn1Q',
    appId: '1:607895076509:web:ce4f82441479e6a965de74',
    messagingSenderId: '607895076509',
    projectId: 'realtime-database-87638',
    authDomain: 'realtime-database-87638.firebaseapp.com',
    databaseURL: 'https://realtime-database-87638-default-rtdb.asia-southeast1.firebasedatabase.app',
    storageBucket: 'realtime-database-87638.appspot.com',
    measurementId: 'G-99P67QVS09',
  );

  static const FirebaseOptions android = FirebaseOptions(
    apiKey: 'AIzaSyAklgoXUVP4hDxQVFPmYmXsR-kRiO4ogJw',
    appId: '1:607895076509:android:558aca988e18542565de74',
    messagingSenderId: '607895076509',
    projectId: 'realtime-database-87638',
    databaseURL: 'https://realtime-database-87638-default-rtdb.asia-southeast1.firebasedatabase.app',
    storageBucket: 'realtime-database-87638.appspot.com',
  );

  static const FirebaseOptions ios = FirebaseOptions(
    apiKey: 'AIzaSyCWIEVKzw2RLGHZLpebBUnqPLk7uZChCd4',
    appId: '1:607895076509:ios:9981d43c03c6cc1165de74',
    messagingSenderId: '607895076509',
    projectId: 'realtime-database-87638',
    databaseURL: 'https://realtime-database-87638-default-rtdb.asia-southeast1.firebasedatabase.app',
    storageBucket: 'realtime-database-87638.appspot.com',
    iosClientId: '607895076509-embpavrt64koakto6cf0u8qi6puq8m33.apps.googleusercontent.com',
    iosBundleId: 'com.example.testGame',
  );

  static const FirebaseOptions macos = FirebaseOptions(
    apiKey: 'AIzaSyCWIEVKzw2RLGHZLpebBUnqPLk7uZChCd4',
    appId: '1:607895076509:ios:9981d43c03c6cc1165de74',
    messagingSenderId: '607895076509',
    projectId: 'realtime-database-87638',
    databaseURL: 'https://realtime-database-87638-default-rtdb.asia-southeast1.firebasedatabase.app',
    storageBucket: 'realtime-database-87638.appspot.com',
    iosClientId: '607895076509-embpavrt64koakto6cf0u8qi6puq8m33.apps.googleusercontent.com',
    iosBundleId: 'com.example.testGame',
  );
}
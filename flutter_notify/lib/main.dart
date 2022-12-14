import 'package:flutter/material.dart';
import 'package:flutter_notify/notify.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Notify.init();

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Container(),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await Notify.showNotification(
            id: 1,
            title: "ok",
            body: "a",
            payload: "o",
          );
        },
        tooltip: 'Increment',
        child: const Icon(
          Icons.notifications,
        ),
      ),
    );
  }
}

import 'package:logger/logger.dart';

var logger = Logger(
  printer: PrettyPrinter(
    printTime: true,
    methodCount: 1,
  ),
);
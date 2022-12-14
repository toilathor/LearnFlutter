part of 'timer_bloc.dart';

abstract class TimerState extends Equatable {
  const TimerState(this.duration);

  final int duration;

  @override
  List<Object> get props => [duration];
}

class TimerInitial extends TimerState {
  const TimerInitial(super.duration);

  @override
  String toString() {
    return 'TimerInitial { duration: $duration }';
  }
}

class TimerRunInProgress extends TimerState {
  const TimerRunInProgress(super.duration);

  @override
  String toString() {
    return 'TimerRunInProgress { duration: $duration }';
  }
}

class TimerRunComplete extends TimerState {
  const TimerRunComplete() : super(0);

  @override
  String toString() {
    return 'TimerRunComplete { duration: $duration }';
  }
}

class TimerRunPause extends TimerState {
  const TimerRunPause(super.duration);

  @override
  String toString() {
    return 'TimerRunPause { duration: $duration }';
  }
}

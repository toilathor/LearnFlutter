class NgayHetHan {
  String? value;
  _Normalized? normalized;
  double? confidence;
  bool? isValid;
  String? valueUnidecode;

  NgayHetHan(
      {this.value,
      this.normalized,
      this.confidence,
      this.isValid,
      this.valueUnidecode});

  NgayHetHan.fromJson(Map<String, dynamic> json) {
    value = json['value'].toString();
    normalized = json['normalized'] != null
        ? new _Normalized.fromJson(json['normalized'])
        : null;
    confidence = json['confidence'];
    isValid = json['is_valid'];
    valueUnidecode = json['value_unidecode'].toString();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['value'] = this.value;
    if (this.normalized != null) {
      data['normalized'] = this.normalized!.toJson();
    }
    data['confidence'] = this.confidence;
    data['is_valid'] = this.isValid;
    data['value_unidecode'] = this.valueUnidecode;
    return data;
  }
}

class _Normalized {
  String? value;
  _Year? year;
  _Month? month;
  _Day? day;
  String? valueUnidecode;

  _Normalized(
      {this.value, this.year, this.month, this.day, this.valueUnidecode});

  _Normalized.fromJson(Map<String, dynamic> json) {
    value = json['value'].toString();
    year = json['year'] != null ? new _Year.fromJson(json['year']) : null;
    month = json['month'] != null ? new _Month.fromJson(json['month']) : null;
    day = json['day'] != null ? new _Day.fromJson(json['day']) : null;
    valueUnidecode = json['value_unidecode'].toString();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['value'] = this.value;
    if (this.year != null) {
      data['year'] = this.year!.toJson();
    }
    if (this.month != null) {
      data['month'] = this.month!.toJson();
    }
    if (this.day != null) {
      data['day'] = this.day!.toJson();
    }
    data['value_unidecode'] = this.valueUnidecode;
    return data;
  }
}

class _Year {
  String? value;

  _Year({this.value});

  _Year.fromJson(Map<String, dynamic> json) {
    value = json['value'].toString();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['value'] = this.value;
    return data;
  }
}

class _Month {
  String? value;

  _Month({this.value});

  _Month.fromJson(Map<String, dynamic> json) {
    value = json['value'].toString();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['value'] = this.value;
    return data;
  }
}

class _Day {
  String? value;

  _Day({this.value});

  _Day.fromJson(Map<String, dynamic> json) {
    value = json['value'].toString();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['value'] = this.value;
    return data;
  }
}

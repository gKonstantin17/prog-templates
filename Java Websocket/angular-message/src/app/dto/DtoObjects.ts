export class SearchText {
  constructor(readonly searchString: String) {}
}

export class DataResult {
  constructor(readonly data: String) {}
}

export class UserProfile {
  // последовательность и тип полей такой же как у bff
  constructor(
    readonly preferredUsername:String,
    readonly email:String,
    readonly id:String,
  ) {}
}

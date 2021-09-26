export class PollOption{
  number!: number;
  option: string;
  votes!: string[];

  constructor(option: string) {
    this.option = option;
  }
}

import {PollOption} from "./PollOption";

export class Poll{
  id!: number;
  question: string;
  options!: PollOption[]
  poll_total_votes!: number;
  voted: boolean = false;

  constructor(question: string) {
    this.question = question;
  }
}

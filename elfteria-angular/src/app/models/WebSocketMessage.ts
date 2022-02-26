export class WebSocketMessage{
  senderUsername!: string;
  content: string;

  constructor(content: string) {
    this.content = content;
  }
}

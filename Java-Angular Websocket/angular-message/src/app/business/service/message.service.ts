import { Injectable } from '@angular/core';
import {Client, IFrame, IMessage} from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import SockJS from 'sockjs-client';

export interface MyEvent {
  date: string;  // или Date, если будете преобразовывать
  descr: string;
}
@Injectable({
  providedIn: 'root'
})
export class MessageService {

  // через клиент работа с сообщениями
  private stompClient: Client | null = null;  // STOMP client instance
  //private  messageSubject = new BehaviorSubject<string[]>([]);
  private  messageSubject = new BehaviorSubject<MyEvent[]>([]);
  public messages$ = this.messageSubject.asObservable();

  connect() {
    // Create a new SockJS connection pointing to the '/ws' endpoint
    const socket = new SockJS('http://localhost:8380/ws');

    // Create a new STOMP Client instance and configure it
    this.stompClient = new Client({
      webSocketFactory: () => socket,  // Use SockJS as the WebSocket factory
      reconnectDelay: 5000,  // Reconnect after 5 seconds if the connection is lost
      debug: (str:string) => {
        console.log(str);  // Log STOMP debug messages for troubleshooting
      },

    });

    // Success connect
     this.stompClient.onConnect = (frame:IFrame) => {
      console.log('Connected: ' + frame);  // Log connection success

      // Subscribe to the public topic '/topic/messages' to receive public messages
      // this.stompClient?.subscribe('/topic/messages', (message) => {
      //   // парсим поле, из объекта Message из Spring
      //   const parsedMessage = JSON.parse(message.body).messageContent;
      //   this.addMessage(parsedMessage);  // Add the received message to the message list
      // });
       this.stompClient?.subscribe('/topic/messages', (message:IMessage) => {
         const receivedEvent: MyEvent = JSON.parse(message.body);
         this.addMessage(receivedEvent);
       });

    };

    // Handle errors reported by the STOMP server
    this.stompClient.onStompError = (frame:IFrame) => {
      console.error('Broker reported error: ' + frame.headers['message']);  // Log the error message
      console.error('Additional details: ' + frame.body);  // Log additional error details
    };

    // Activate the client (i.e., initiate the WebSocket connection)
    this.stompClient.activate();
  }
  // Function to send a message to the server via the WebSocket connection
  sendMessage(message: string) {
    if (this.stompClient?.connected) {  // проверка соединения
      this.stompClient?.publish({ // отправить
        destination: '/app/message',  // куда отправлять
        body: JSON.stringify({ messageContent: message })  // что отправлять
      });

    }
  }


  sendEvent(event: MyEvent) {
    if (this.stompClient?.connected) {
      this.stompClient.publish({
        destination: '/app/events',
        body: JSON.stringify(event)
      });

    }
  }
  // private addMessage(message: string) {
  //   // достаем messages: string[] = []; из компонента
  //   const currentMessages = this.messageSubject.value;
  //   console.log("MESSAGE ADDED : " + message)
  //   // пересоздаем новый массив, чтоб до ангуляра дошло, что изменения были
  //   this.messageSubject.next([...currentMessages, message]);  // Update the message list with the new message
  // }
  private addMessage(message: MyEvent) {
    const currentMessages = this.messageSubject.value;
    this.messageSubject.next([...currentMessages, message]);
  }
}

// if (this.stompClient?.connected)
// эквивалетно
// if (this.stompClient && this.stompClient.connected)
// if (this.stompClient !== null && this.stompClient !== undefined && this.stompClient.connected)

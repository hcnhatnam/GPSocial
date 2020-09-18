import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

export default class PubSubMessageClass {
  constructor(topicSendUser, onMessageReceived, isSubscribeSelf = false) {
    try {
      this.onMessageReceived = onMessageReceived
      this.topicSendUser = topicSendUser
      this.socket = new SockJS("http://localhost:8000/ws");

      var options = {
        debug: false
        // protocols: Stomp.VERSIONS.supportedProtocols()
      };
      this.stompClient = Stomp.over(this.socket, options);

      this.stompClient.connect({}, () => {
        this.onConnected(this, isSubscribeSelf)
      }, this.onError);
    } catch (error) {
      console.log(error);
    }
  }
  onConnected(that, isSubscribeSelf) {
    if (isSubscribeSelf) {
      console.log("subcibetopic:" + "/topic/private_" +
        that.topicSendUser)
      that.subcibetopic("/topic/private_" +
        that.topicSendUser);
    }
    // this.subcibetopic("/topic/private_" + this.sender);

    // Tell your username to the server
    // that.stompClient.send("/app/chat.addUser",
    //   JSON.stringify({
    //     sender: that.sender,
    //     type: "JOIN",
    //     content: "hello!!!",
    //     reciver: that.reciver
    //   }, {})
    // );
  }
  subcibetopic(topicName) {
    console.log("subscribe Topic: ", this, topicName);
    this.stompClient.subscribe(topicName, this.onMessageReceived);
  }
  onError(error) {
    console.log(error);
  }

  sendMessage(message, sender, reciver) {
    try {
      const messageContent = JSON.stringify(message);
      if (messageContent !== "" && this.stompClient) {
        var chatMessage = {
          sender: sender,
          reciver: reciver,
          content: messageContent,
          type: "CHAT"
        };
        console.log(
          "sendMessageToUser",
          JSON.stringify(chatMessage)
        );
        this.stompClient.send(
          "/app/chat.sendMessageToUser",
          JSON.stringify(chatMessage), {}
        );
      }
    } catch (error) {
      console.log(error);
    }
  }
  // sendMessageToUser(messageObject) {
  //   try {
  //     const message = JSON.stringify(messageObject);
  //     console.log("send:" + message);
  //     const messageContent = message;
  //     if (messageContent !== "" && this.stompClient) {
  //       var chatMessage = {
  //         sender: this.sender,
  //         reciver: this.reciver,
  //         content: messageContent,
  //         type: "CHAT"
  //       };
  //       console.log(
  //         "JSON.stringify(chatMessage)",
  //         JSON.stringify(chatMessage)
  //       );
  //       this.stompClient.send(
  //         "/app/chat.sendMessageToUser",
  //         JSON.stringify(chatMessage), {}
  //       );
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // }
}

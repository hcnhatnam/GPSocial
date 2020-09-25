<template>
  <div>
    <!-- <el-button @click="sendMessage()">send message</el-button> -->
  </div>
</template>

<script>
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

export default {
  props: {
    message: {
      type: Object,
      default: null
    },
    sender: {
      type: String,
      default: "sender"
    },
    reciver: {
      type: String,
      default: "reciver"
    }
  },
  data() {
    return {
      socket: null,
      stompClient: null
    };
  },
  watch: {
    message: function() {
      console.log("watch:", this.message);
      this.sendMessageToUser(this.message);
    }
  },
  mounted() {
    try {
      this.socket = new SockJS(process.env.VUE_APP_BASE_API + ":8000/ws");
      var options = {
        debug: false
        // protocols: Stomp.VERSIONS.supportedProtocols()
      };
      this.stompClient = Stomp.over(this.socket, options);

      this.stompClient.connect({}, this.onConnected, this.onError);
    } catch (error) {
      console.log(error);
    }
  },

  methods: {
    subcibetopic(topicName) {
      console.log("subscribe Topic: " + topicName);
      this.stompClient.subscribe(topicName, this.onMessageReceived);
    },
    onConnected() {
      this.subcibetopic("/topic/private_" + this.sender);

      // Tell your username to the server
      this.stompClient.send(
        "/app/chat.addUser",
        JSON.stringify(
          {
            sender: this.sender,
            type: "JOIN",
            content: "hello!!!",
            reciver: this.reciver
          },
          {}
        )
      );
    },

    onError(error) {
      console.log(error);
      // connectingElement.textContent =
      //   "Could not connect to WebSocket server. Please refresh this page to try again!";
      // connectingElement.style.color = "red";
    },

    sendMessage(message) {
      try {
        console.log("send:" + message);
        const messageContent = message;
        if (messageContent !== "" && this.stompClient) {
          var chatMessage = {
            sender: this.username,
            content: messageContent,
            type: "CHAT"
          };
          console.log(
            "JSON.stringify(chatMessage)",
            JSON.stringify(chatMessage)
          );
          this.stompClient.send(
            "/app/chat.sendMessage",
            JSON.stringify(chatMessage),
            {}
          );
        }
      } catch (error) {
        console.log(error);
      }
    },
    sendMessageToUser(messageObject) {
      try {
        const message = JSON.stringify(messageObject);
        console.log("send:" + message);
        const messageContent = message;
        if (messageContent !== "" && this.stompClient) {
          var chatMessage = {
            sender: this.sender,
            reciver: this.reciver,
            content: messageContent,
            type: "CHAT"
          };
          console.log(
            "JSON.stringify(chatMessage)",
            JSON.stringify(chatMessage)
          );
          this.stompClient.send(
            "/app/chat.sendMessageToUser",
            JSON.stringify(chatMessage),
            {}
          );
        }
      } catch (error) {
        console.log(error);
      }
    },
    onMessageReceived(payload) {
      var message = JSON.parse(payload.body);

      // var messageElement = document.createElement("li");

      // if (message.type === "JOIN") {
      //   messageElement.classList.add("event-message");
      //   message.content = message.sender + " joined!";
      // } else if (message.type === "LEAVE") {
      //   messageElement.classList.add("event-message");
      //   message.content = message.sender + " left!";
      // } else {
      //   messageElement.classList.add("chat-message");

      //   var avatarElement = document.createElement("i");
      //   var avatarText = document.createTextNode(message.sender[0]);
      //   avatarElement.appendChild(avatarText);
      //   avatarElement.style["background-color"] = getAvatarColor(
      //     message.sender
      //   );

      //   messageElement.appendChild(avatarElement);

      //   var usernameElement = document.createElement("span");
      //   var usernameText = document.createTextNode(message.sender);
      //   usernameElement.appendChild(usernameText);
      //   messageElement.appendChild(usernameElement);
      // }
      return this.$emit("onMessageReceived", message);
    }

    // getAvatarColor(messageSender) {
    //   var hash = 0;
    //   for (var i = 0; i < messageSender.length; i++) {
    //     hash = 31 * hash + messageSender.charCodeAt(i);
    //   }

    //   var index = Math.abs(hash % colors.length);
    //   return colors[index];
    // }
  }
};
</script>

<style></style>

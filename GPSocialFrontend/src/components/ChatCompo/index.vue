<template>
  <div class="app-container">
    <!-- <el-input v-model="sender" placeholder="Please Sender" />
    <el-input v-model="reciver" placeholder="Please Reciver" />-->
    <!-- <el-button @click="isStart=true">Test</el-button> -->
    <PubSubMessage
      :sender="sender"
      :reciver="reciver"
      :message="message"
      @onMessageReceived="onMessageReceived"
    />
    <VueChat
      v-if="isStart || isReady"
      :participants="participants"
      :message="messageRecived"
      @sendMessage="sendMessage"
    />
  </div>
</template>

<script>
import { getList } from "@/api/table";

export default {
  components: {
    PubSubMessage: () => import("@/components/PubSubMessage"),
    VueChat: () => import("@/components/VueChat")
  },
  props: {
    sender: {
      type: String,
      default: ""
    },
    reciver: {
      type: String,
      default: ""
    },
    isStart: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      isReady: false,
      message: {},
      messageRecived: null,
      senderObject: {}
    };
  },
  computed: {
    participants() {
      return [
        {
          id: this.reciver,
          name: this.reciver,
          imageUrl:
            "https://img.icons8.com/fluent/48/000000/user-male-circle.png"
        },
        {
          id: `me`,
          name: this.sender,
          imageUrl:
            "https://img.icons8.com/material-two-tone/48/000000/user-male-circle.png"
        }
      ];
    }
  },
  mounted() {
    console.log("OKKKKKKKKKKKKKKK", this.sender, this.reciver);
  },
  methods: {
    onMessageReceived(messageRecived) {
      this.isReady = true;
      if (this.reciver === "") {
        this.$emit("assignReciver", messageRecived.sender);
      }
      const content = JSON.parse(messageRecived.content);
      this.messageRecived = {
        type: Object.keys(content)[0],
        author: messageRecived.sender,
        data: content
      };
      console.log(" this.messageRecived", this.messageRecived);
    },
    sendMessage(message) {
      this.message = message.data;
      console.log(message);
    }
  }
};
</script>

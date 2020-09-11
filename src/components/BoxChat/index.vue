<template>
  <div class="app-container">
    <VueChat
      v-if="isStart || isReady"
      :participants="participants"
      :message="messageReciverChild"
      @sendMessage="sendMessage"
    />
  </div>
</template>

<script>
import { getList } from "@/api/table";
import PubSubMessageClass from "@/utils/pubsubmessage";

export default {
  components: {
    // PubSubMessage: () => import("@/components/PubSubMessage"),
    VueChat: () => import("@/components/VueChat")
  },
  props: {
    pubsubmessage: {
      type: Object, // PubSubMessageClass,
      default: function() {
        return {};
      }
    },
    me: {
      type: String,
      default: ""
    },

    sender: {
      type: String,
      default: ""
    },
    reciver: {
      type: String,
      default: ""
    },
    messageReciver: {
      type: Object,
      default: null
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
      senderObject: {},
      messageReciverChild: null
    };
  },
  computed: {
    participants() {
      return [
        {
          id: this.me,
          name: this.me,
          imageUrl:
            "https://img.icons8.com/fluent/48/000000/user-male-circle.png"
        },
        {
          id: this.otherme,
          name: this.otherme,
          imageUrl:
            "https://img.icons8.com/material-two-tone/48/000000/user-male-circle.png"
        }
      ];
    },
    otherme() {
      if (this.sender !== this.me) {
        return this.sender;
      } else {
        return this.reciver;
      }
    }
  },
  // watch: {
  //   messageReciver: function() {
  //     console.log("watch:", this.messageReciver);
  //     // this.sendMessageToUser(this.message);
  //   }
  // },
  watch: {
    messageReciver: function() {
      console.log("onMessageReceived", this.messageReciver, this.participants);
      this.onMessageReceived(this.messageReciver);
    }
  },
  mounted() {
    console.log(
      "BOX CHAT",
      this.pubsubmessage,
      "this.sender",
      this.sender,
      "reciver:",
      this.reciver,
      "  this.messageReciver",
      this.messageReciver
    );
    if (this.messageReciver !== null) {
      this.onMessageReceived(this.messageReciver);
    }
  },
  methods: {
    onMessageReceived(messageReciver) {
      this.isReady = true;
      if (this.reciver === "") {
        this.$emit("assignReciver", messageReciver.sender);
      }
      const content = JSON.parse(messageReciver.content);
      this.messageReciverChild = {
        type: Object.keys(content.data)[0],
        author: messageReciver.sender,
        data: content.data
      };
      console.log(
        " this.messageReciver",
        this.messageReciverChild,
        this.messageReciver
      );
    },
    sendMessage(message) {
      this.message = message.data;
      this.pubsubmessage.sendMessage(message, this.me, this.otherme);
      console.log(message);
    }
  }
};
</script>

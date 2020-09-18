<template>
  <div class="window-container">
    <div class="chat-forms">
      <form v-if="addNewRoom" @submit.prevent="createRoom">
        <el-input
          v-model="addRoomUsername"
          placeholder="To create a room"
          class="input-with-select"
        >
          <template slot="prepend">Username</template>
          <!-- <template slot="append">Username</template> -->
          <el-button
            slot="append"
            class="addRoom"
            :disabled="disableForm || !addRoomUsername"
            icon="el-icon-edit"
          />
          <el-button slot="append" icon="el-icon-close" @click="addNewRoom = false" />

          <!-- <el-button slot="append" @click="addNewRoom = false">Cancel</el-button> -->
        </el-input>
      </form>

      <form v-if="inviteRoomId" @submit.prevent="addRoomUser">
        <el-input v-model="invitedUserId" type="text" placeholder="Add user to the room" />
        <button type="submit" :disabled="disableForm || !invitedUserId">Add User</button>
        <button class="button-cancel" @click="inviteRoomId = null">Cancel</button>
      </form>

      <form v-if="removeRoomId" @submit.prevent="deleteRoomUser">
        <select v-model="removeUserId">
          <option default value>Select User</option>
          <option v-for="user in removeUsers" :key="user._id" :value="user._id">{{ user.username }}</option>
        </select>
        <button type="submit" :disabled="disableForm || !removeUserId">Remove User</button>
        <button class="button-cancel" @click="removeRoomId = null">Cancel</button>
      </form>
    </div>

    <chat-window
      :responsive-breakpoint="500"
      :theme="theme"
      :styles="styles"
      :current-user-id="currentUserId"
      :rooms="rooms"
      :loading-rooms="loadingRooms"
      :messages="messages"
      :messages-loaded="messagesLoaded"
      :menu-actions="menuActions"
      @fetchMessages="fetchMessages"
      @sendMessage="sendMessage"
      @editMessage="editMessage"
      @deleteMessage="deleteMessage"
      @openFile="openFile"
      @addRoom="addRoom"
      @menuActionHandler="menuActionHandler"
      @messageActionHandler="messageActionHandler"
      @sendMessageReaction="sendMessageReaction"
      @typingMessage="typingMessage"
    />
  </div>
</template>

<script>
import {
  firebase,
  roomsRef,
  usersRef,
  filesRef,
  deleteDbField,
  existUser,
  existRoom
} from "@/firestore";
import { EventBus } from "@/main";
import { parseTimestamp, isSameDay } from "@/utils/dates";
// import ChatWindow from './../../src/ChatWindow'
import ChatWindow from "vue-advanced-chat";
import "vue-advanced-chat/dist/vue-advanced-chat.css";

export default {
  components: {
    ChatWindow
  },

  props: {
    currentUserId: {
      type: String,
      default: ""
    },

    theme: {
      type: String,
      default: "light"
    },
    invitedUserIdProp: {
      type: String,
      default: ""
    }
  },

  data() {
    return {
      perPage: 20,
      rooms: [],
      loadingRooms: true,
      selectedRoom: null,
      messages: [],
      messagesLoaded: false,
      start: null,
      end: null,
      roomsListeners: [],
      listeners: [],
      disableForm: false,
      addNewRoom: null,
      addRoomUsername: "",
      inviteRoomId: null,
      invitedUserId: "",
      removeRoomId: null,
      removeUserId: "",
      removeUsers: [],
      menuActions: [
        // { name: "inviteUser", title: "Invite User" },
        // { name: "removeUser", title: "Remove User" }
        { name: "deleteRoom", title: "Delete Room" }
      ],
      styles: { container: { borderRadius: "4px" } }
    };
  },
  mounted() {
    this.invitedUserId = this.invitedUserIdProp;
    this.createRoom();
    this.fetchRooms();
    this.updateUserOnlineStatus();
  },

  destroyed() {
    this.resetRooms();
  },

  methods: {
    messagesRef(roomId) {
      return roomsRef.doc(roomId).collection("messages");
    },
    resetRooms() {
      this.loadingRooms = true;
      this.rooms = [];
      this.roomsListeners.forEach(listener => listener());
      this.resetMessages();
    },

    resetMessages() {
      this.messages = [];
      this.messagesLoaded = false;
      this.start = null;
      this.end = null;
      this.listeners.forEach(listener => listener());
      this.listeners = [];
    },

    async fetchRooms() {
      this.resetRooms();

      const query = roomsRef.where(
        "users",
        "array-contains",
        this.currentUserId
      );

      const rooms = await query.get();
      const roomList = [];
      const rawRoomUsers = [];
      const rawMessages = [];
      // console.log("rooms", rooms);
      rooms.forEach(room => {
        roomList[room.id] = { ...room.data(), users: [] };
        console.log(room.data(), roomList);
        const rawUsers = [];
        room.data().users.map(userId => {
          const promise = usersRef
            .doc(userId)
            .get()
            .then(user => {
              return {
                ...user.data(),
                ...{
                  roomId: room.id,
                  username: user.data().username
                }
              };
            });

          rawUsers.push(promise);
        });

        rawUsers.map(users => rawRoomUsers.push(users));
        rawMessages.push(this.getLastMessage(room));
      });

      const users = await Promise.all(rawRoomUsers);

      users.map(user => roomList[user.roomId].users.push(user));
      console.log("roomList", roomList);
      const roomMessages = await Promise.all(rawMessages).then(messages => {
        return messages.map(message => {
          return {
            lastMessage: this.formatLastMessage(message),
            roomId: message.roomId
          };
        });
      });

      roomMessages.map(
        ms => (roomList[ms.roomId].lastMessage = ms.lastMessage)
      );

      const formattedRooms = [];

      Object.keys(roomList).forEach(key => {
        const room = roomList[key];

        const roomContacts = room.users.filter(
          user => user._id !== this.currentUserId
        );

        room.roomName =
          roomContacts.map(user => user.username).join(", ") || "Myself";

        const roomAvatar =
          roomContacts.length === 1 && roomContacts[0].avatar
            ? roomContacts[0].avatar
            : require("@/assets/logo.png");

        formattedRooms.push({
          ...{
            roomId: key,
            avatar: roomAvatar,
            ...room
          }
        });
      });

      console.log("  this.rooms ", this.rooms, formattedRooms);

      this.rooms = formattedRooms; // this.rooms.concat(formattedRooms);
      console.log();
      this.loadingRooms = false;
      this.rooms.map((room, index) => this.listenLastMessage(room, index));

      this.listenUsersOnlineStatus();
      this.listenRoomsTypingUsers(query);
    },

    getLastMessage(room) {
      return this.messagesRef(room.id)
        .orderBy("timestamp", "desc")
        .limit(1)
        .get()
        .then(messages => {
          const array = [];
          messages.forEach(m => array.push(m.data()));
          return { ...array[0], roomId: room.id };
        });
    },

    listenLastMessage(room, index) {
      const listener = this.messagesRef(room.roomId)
        .orderBy("timestamp", "desc")
        .limit(1)
        .onSnapshot(messages => {
          messages.forEach(message => {
            const lastMessage = this.formatLastMessage(message.data());
            this.rooms[index].lastMessage = lastMessage;
          });
        });

      this.roomsListeners.push(listener);
    },

    formatLastMessage(message) {
      if (!message.timestamp) return;
      const date = new Date(message.timestamp.seconds * 1000);
      const timestampFormat = isSameDay(date, new Date())
        ? "HH:mm"
        : "DD/MM/YY";

      let timestamp = parseTimestamp(message.timestamp, timestampFormat);
      if (timestampFormat === "HH:mm") timestamp = `Today, ${timestamp}`;

      let content = message.content;
      if (message.file) content = `${message.file.name}.${message.file.type}`;

      return {
        ...message,
        ...{
          content,
          timestamp,
          date: message.timestamp.seconds,
          seen: message.sender_id === this.currentUserId ? message.seen : null,
          new:
            message.sender_id !== this.currentUserId &&
            (!message.seen || !message.seen[this.currentUserId])
        }
      };
    },

    fetchMessages({ room, options = {} }) {
      if (options.reset) this.resetMessages();

      if (this.end && !this.start) return (this.messagesLoaded = true);

      const ref = this.messagesRef(room.roomId);

      let query = ref.orderBy("timestamp", "desc").limit(this.perPage);

      if (this.start) query = query.startAfter(this.start);

      this.selectedRoom = room.roomId;

      query.get().then(messages => {
        if (this.selectedRoom !== room.roomId) return;

        if (messages.empty) this.messagesLoaded = true;

        if (this.start) this.end = this.start;
        this.start = messages.docs[messages.docs.length - 1];

        let listenerQuery = ref.orderBy("timestamp");

        if (this.start) listenerQuery = listenerQuery.startAfter(this.start);
        if (this.end) listenerQuery = listenerQuery.endAt(this.end);

        if (options.reset) this.messages = [];

        messages.forEach(message => {
          const formattedMessage = this.formatMessage(room, message);
          this.messages.unshift(formattedMessage);
        });

        const listener = listenerQuery.onSnapshot(snapshots => {
          this.listenMessages(snapshots, room);
        });
        this.listeners.push(listener);
      });
    },

    listenMessages(messages, room) {
      messages.forEach(message => {
        // console.log(message);
      });
      // console.log("listenMessages", room);
      messages.forEach(message => {
        const formattedMessage = this.formatMessage(room, message);
        const messageIndex = this.messages.findIndex(m => m._id === message.id);

        if (messageIndex === -1) {
          this.messages = this.messages.concat([formattedMessage]);
        } else {
          this.$set(this.messages, messageIndex, formattedMessage);
        }

        this.markMessagesSeen(room, message);
      });
    },

    markMessagesSeen(room, message) {
      if (
        message.data().sender_id !== this.currentUserId &&
        (!message.data().seen || !message.data().seen[this.currentUserId])
      ) {
        this.messagesRef(room.roomId)
          .doc(message.id)
          .update({
            [`seen.${this.currentUserId}`]: new Date()
          });
      }
    },

    formatMessage(room, message) {
      const senderUser = room.users.find(
        user => message.data().sender_id === user._id
      );

      const { sender_id, timestamp } = message.data();

      return {
        ...message.data(),
        ...{
          sender_id,
          _id: message.id,
          seconds: timestamp.seconds,
          timestamp: parseTimestamp(timestamp, "HH:mm"),
          date: parseTimestamp(timestamp, "DD MMMM YYYY"),
          username: senderUser ? senderUser.username : null
        }
      };
    },

    async sendMessage({ content, roomId, file, replyMessage }) {
      const message = {
        sender_id: this.currentUserId,
        content,
        timestamp: new Date()
      };

      if (file) {
        message.file = {
          name: file.name,
          size: file.size,
          type: file.type,
          url: file.localUrl
        };
      }

      if (replyMessage) {
        message.replyMessage = {
          _id: replyMessage._id,
          content: replyMessage.content,
          sender_id: replyMessage.sender_id
        };

        if (replyMessage.file) {
          message.replyMessage.file = replyMessage.file;
        }
      }

      const { id } = await this.messagesRef(roomId).add(message);
      EventBus.$emit(
        "sendMessage",
        this.rooms.find(room => room.roomId === roomId)
      );
      console.log("sendMess", { content, roomId, file, replyMessage });
      if (file) this.uploadFile({ file, messageId: id, roomId });
    },

    openFile(message) {
      window.open(message.file.url, "_blank");
    },

    async editMessage({ messageId, newContent, roomId, file }) {
      const newMessage = { edited: new Date() };
      newMessage.content = newContent;

      if (file) {
        newMessage.file = {
          name: file.name,
          size: file.size,
          type: file.type,
          url: file.url || file.localUrl
        };
      } else {
        newMessage.file = deleteDbField;
      }

      await this.messagesRef(roomId)
        .doc(messageId)
        .update(newMessage);
    },

    async deleteMessage({ messageId, roomId }) {
      await this.messagesRef(roomId)
        .doc(messageId)
        .update({ deleted: new Date() });
    },

    async uploadFile({ file, messageId, roomId }) {
      const uploadFileRef = filesRef
        .child(this.currentUserId)
        .child(messageId)
        .child(`${file.name}.${file.type}`);

      await uploadFileRef.put(file.blob, { contentType: file.type });
      const url = await uploadFileRef.getDownloadURL();

      await this.messagesRef(roomId)
        .doc(messageId)
        .update({
          "file.url": url
        });
    },

    menuActionHandler({ action, roomId }) {
      switch (action.name) {
        case "inviteUser":
          return this.inviteUser(roomId);
        case "removeUser":
          return this.removeUser(roomId);
        case "deleteRoom":
          return this.deleteRoom(roomId);
      }
    },

    messageActionHandler() {
      // do something
    },

    async sendMessageReaction({ reaction, remove, messageId, roomId }) {
      const dbAction = remove
        ? firebase.firestore.FieldValue.arrayRemove(this.currentUserId)
        : firebase.firestore.FieldValue.arrayUnion(this.currentUserId);

      await this.messagesRef(roomId)
        .doc(messageId)
        .update({
          [`reactions.${reaction.name}`]: dbAction
        });
    },

    typingMessage({ message, roomId }) {
      const dbAction = message
        ? firebase.firestore.FieldValue.arrayUnion(this.currentUserId)
        : firebase.firestore.FieldValue.arrayRemove(this.currentUserId);

      roomsRef.doc(roomId).update({
        typingUsers: dbAction
      });
    },

    async listenRoomsTypingUsers(query) {
      query.onSnapshot(rooms => {
        rooms.forEach(room => {
          const foundRoom = this.rooms.find(r => r.roomId === room.id);
          if (foundRoom) foundRoom.typingUsers = room.data().typingUsers;
        });
      });
    },

    updateUserOnlineStatus() {
      const userStatusRef = firebase
        .database()
        .ref("/status/" + this.currentUserId);

      const isOfflineData = {
        state: "offline",
        last_changed: firebase.database.ServerValue.TIMESTAMP
      };

      const isOnlineData = {
        state: "online",
        last_changed: firebase.database.ServerValue.TIMESTAMP
      };

      firebase
        .database()
        .ref(".info/connected")
        .on("value", snapshot => {
          if (snapshot.val() == false) return;

          userStatusRef
            .onDisconnect()
            .set(isOfflineData)
            .then(() => {
              userStatusRef.set(isOnlineData);
            });
        });
    },

    listenUsersOnlineStatus() {
      this.rooms.map(room => {
        room.users.map(user => {
          firebase
            .database()
            .ref("/status/" + user._id)
            .on("value", snapshot => {
              if (!snapshot.val()) return;

              const timestampFormat = isSameDay(
                new Date(snapshot.val().last_changed),
                new Date()
              )
                ? "HH:mm"
                : "DD MMMM, HH:mm";

              const timestamp = parseTimestamp(
                new Date(snapshot.val().last_changed),
                timestampFormat
              );

              const last_changed =
                timestampFormat === "HH:mm" ? `today, ${timestamp}` : timestamp;

              user.status = { ...snapshot.val(), last_changed };

              const roomIndex = this.rooms.findIndex(
                r => room.roomId === r.roomId
              );

              this.$set(this.rooms, roomIndex, room);
            });
        });
      });
    },

    addRoom() {
      this.resetForms();
      this.addNewRoom = true;
    },

    async createRoom() {
      this.disableForm = true;
      const user = {
        _id: this.invitedUserId,
        username: this.invitedUserId,
        avatar:
          "https://vignette.wikia.nocookie.net/teamavatarone/images/4/45/Yoda.jpg/revision/latest?cb=20130224160049"
      };
      const isExistUser = await existUser(user._id);

      if (!isExistUser) {
        console.log("addUser", user, this.invitedUserId);
        await usersRef.doc(user._id).set(user);
      }

      const id = user._id;
      var n = id.localeCompare(this.currentUserId);
      let _id = this.currentUserId + "_" + id;
      if (n === -1) {
        _id = id + "_" + this.currentUserId;
      }
      const room = {
        _id: _id,
        users: [this.currentUserId, id]
      };
      const isExistRoom = await existRoom(room._id);
      console.log("isExistRoom", isExistRoom);
      if (!isExistRoom) {
        await roomsRef.doc(room._id).set(room);
      }
      // await roomsRef.add({ users: [id, this.currentUserId] });

      this.addNewRoom = false;
      this.addRoomUsername = "";
      this.fetchRooms();
    },

    inviteUser(roomId) {
      this.resetForms();
      this.inviteRoomId = roomId;
    },

    // async addRoomUser() {
    //   this.disableForm = true;
    //   const user = {
    //     _id: this.invitedUserId,
    //     username: this.invitedUserId,
    //     avatar:
    //       "https://vignette.wikia.nocookie.net/teamavatarone/images/4/45/Yoda.jpg/revision/latest?cb=20130224160049"
    //   };
    //   await usersRef.doc(this.invitedUserId).set(user);
    //   const id = user._id;
    //   await roomsRef
    //     .doc(this.inviteRoomId)
    //     .update({ users: firebase.firestore.FieldValue.arrayUnion(id) });

    //   this.inviteRoomId = null;
    //   this.invitedUserId = "";
    //   this.fetchRooms();
    // },

    removeUser(roomId) {
      this.resetForms();
      this.removeRoomId = roomId;
      this.removeUsers = this.rooms.find(room => room.roomId === roomId).users;
    },

    async deleteRoomUser() {
      this.disableForm = true;

      await roomsRef.doc(this.removeRoomId).update({
        users: firebase.firestore.FieldValue.arrayRemove(this.removeUserId)
      });

      this.removeRoomId = null;
      this.removeUserId = "";
      this.fetchRooms();
    },

    async deleteRoom(roomId) {
      const ref = this.messagesRef(roomId);

      ref.get().then(res => {
        if (res.empty) return;
        res.docs.map(doc => ref.doc(doc.id).delete());
      });

      await roomsRef.doc(roomId).delete();

      this.fetchRooms();
    },

    resetForms() {
      this.disableForm = false;
      this.addNewRoom = null;
      this.addRoomUsername = "";
      this.inviteRoomId = null;
      this.invitedUserId = "";
      this.removeRoomId = null;
      this.removeUserId = "";
    }
  }
};
</script>

<style lang="scss" scoped>
.window-container {
  width: 100%;
}

.chat-forms {
  padding-bottom: 20px;

  form {
    padding-top: 20px;
  }

  input {
    padding: 5px;
    width: 180px;
    height: 21px;
    border-radius: 4px;
    border: 1px solid #d2d6da;
    outline: none;
    font-size: 14px;
    vertical-align: middle;

    &::placeholder {
      color: #9ca6af;
    }
  }

  button {
    // background: #1976d2;
    // color: #fff;
    // outline: none;
    // cursor: pointer;
    // border-radius: 4px;
    // padding: 8px 12px;
    // margin-left: 10px;
    // border: none;
    // font-size: 14px;
    // transition: 0.3s;
    // vertical-align: middle;

    &:active {
      opacity: 0.6;
    }

    &:disabled {
      cursor: no-drop;
      background: #e2e2e2;
      opacity: 0.6;
    }
  }
  .addRoom {
    margin-right: 0.5px;
    &:hover {
      opacity: 0.8;
      border: 1px solid black;
      border-radius: 0px;
    }
  }
  .button-cancel {
    color: #a8aeb3;
    background: none;
    margin-left: 5px;
  }

  select {
    vertical-align: middle;
    height: 33px;
    width: 120px;
    font-size: 13px;
  }
  .el-select .el-input {
    width: 110px;
  }
  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
}
</style>

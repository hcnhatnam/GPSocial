<template>
  <div v-if="screen !== null" class="full-height">
    <!-- <el-button style="margin:3em" @click="clickTest()">Test</el-button>

    <el-badge :value="12" class="item">
      <el-dropdown trigger="click" @command="handleCommand">
        <el-button circle type="warning">
          <i class="el-icon-message" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item v-for="[key, val] of getmapPubSubMessage" :key="key" :command="key">
            <el-divider content-position="left">
              <span class="title">{{ val.reciver }}</span>
            </el-divider>
            <span>I cannot choose the best. The best chooses me.</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-badge>-->
    <GmapMap
      :center="centerMap"
      :zoom="zoom"
      map-type-id="terrain"
      :style="getStyleMap()"
      @click="mark"
    >
      <GmapMarker
        v-for="(m, index) in markers"
        :key="index"
        :position="m.position"
        :icon="m.icon"
        :clickable="true"
        :label="m.name"
        :draggable="true"
        @click="clickMarker(m)"
      />
    </GmapMap>
    <el-button v-for="(m, index) in markers" :key="index" @click="clickMarker(m)">{{ m.name }}</el-button>

    <el-dialog
      title="Chat"
      :show-close="false"
      :visible.sync="dialogVisible"
      width="50%"
      :fullscreen="fullscreen"
      :before-close="handleClose"
    >
      <span slot="title" style="float:right;clear:both;">
        <el-button class="button-light" size="small" @click="theme = 'light'">Light</el-button>
        <el-button class="button-dark" size="small" @click="theme = 'dark'">Dark</el-button>
        <el-button icon="el-icon-full-screen" size="small" @click="fullscreen=!fullscreen" />

        <el-button icon="el-icon-close" type="danger" size="small" @click="dialogVisible=false" />
      </span>
      <Chat
        :invited-user-id="chooseUser"
        :users="users"
        :current-user-id="user._id"
        :theme="theme"
      />
    </el-dialog>
  </div>
</template>

<script>
class Marker {
  constructor(info, city, lat, lng, name, user) {
    this.city = city;
    this.info = info;
    this.position = {
      lat: lat,
      lng: lng
    };
    this.name = name;
    this.user = user;

    // this.icon = "https://img.icons8.com/bubbles/50/000000/street-view.png";
  }
}
import { getInfoFromExtApi, getInfoWithIp } from "@/api/apiExt";
import { getOnlineUsers } from "@/api/apiInfo";
import store from "@/store";
import PubSubMessageClass from "@/utils/pubsubmessage";
import UserChat from "@/utils/UserChat";
import { EventBus } from "@/main";
import { findUserByEmail, createUser } from "@/firestore";

export default {
  components: {
    Chat: () => import("@/components/Chat")
  },
  data() {
    return {
      keyMap: 0,
      ratioMap: 0.7,
      screen: null,
      markers: [],
      zoom: 2,
      centerMap: { lat: 0, lng: 0 },
      message: "test",
      mapUser: {},
      reciver: "",
      keyReload: 0,
      chooseUser: "",
      users: [],
      dialogVisible: false,
      pubsubmessage: null,
      fullscreen: false,
      theme: "light"
    };
  },
  computed: {
    user() {
      return this.$store.state.user.user;
    },
    userInfo() {
      return this.$store.state.user;
    },
    getCenter() {
      if (this.markers.length === 0) {
        return {
          lat: 0,
          lng: 0
        };
      }
      const mark = this.markers[this.markers.length - 1];
      return mark.position;
    }
  },
  watch: {
    markers: function() {
      if (this.markers.length === 0) {
        return {
          lat: 0,
          lng: 0
        };
      }
      const mark = this.markers[this.markers.length - 1];
      this.centerMap = mark.position;
      for (const marker of this.markers) {
        const user = this.users.find(x => x._id === marker.user._id);
        if (user === undefined) {
          this.users.push(
            new UserChat(
              marker.user._id,
              marker.user.username,
              marker.user.profilePicLink, // "https://img.icons8.com/officel/16/000000/circled-user-male-skin-type-6.png",
              marker.user.email
            )
          );
        }
      }
      this.keyMap++;
    }
  },
  mounted() {
    console.log("this.user", this.user);
    findUserByEmail(this.user.email).then(userGet => {
      if (userGet == null) {
        const _id = createUser(this.user);
        this.user._id = _id;
      } else {
        this.user._id = userGet._id;
      }
      this.screen = screen;
      console.log("use========", this.user, this.userInfo);
      getInfoWithIp(this.userInfo.ip).then(resp => {
        console.log("resp.data", resp.data);
        this.markLocaionWithIp(resp.data.info, this.user.email, this.user);
      });
      this.timmerGetInfo();
      this.users.push(
        new UserChat(
          this.user._id,
          this.user.username,
          this.user.profilePicLink,
          this.user.email
        )
      );
      this.pubsubmessage = new PubSubMessageClass(
        this.user.email,
        this.onMessageReceived,
        true
      );
      EventBus.$on("sendMessage", room => {
        for (const user of room.users) {
          if (user.username !== this.user.name) {
            this.pubsubmessage.sendMessage(
              "Message comming",
              this.user.email,
              user.email
            );
          }
        }
      });
    });
  },
  methods: {
    onMessageReceived(payload) {
      const payloadReciver = JSON.parse(payload.body);
      // const reciver = payloadReciver.reciver;
      // const objReciver = this.mapPubSubMessage.get(reciver);
      console.log("this.currentChatUser", this.user.name, payloadReciver);
      this.chooseUser = payloadReciver.sender;
      this.dialogVisible = true;
    },
    clickChooseUser(maker) {
      this.chooseUser = maker.user._id;
      this.dialogVisible = true;
    },
    timmerGetInfo() {
      setInterval(() => {
        getOnlineUsers(this.user.email).then(resp => {
          if (resp.error === 0) {
            const onlineUsers = resp.data.onlineUsers;
            this.mapUser = {};
            this.maker = {};

            for (const email of Object.keys(onlineUsers)) {
              // console.log("email", email);
              const user = onlineUsers[email].user;
              findUserByEmail(user.email).then(userGet => {
                if (userGet == null) {
                  const _id = createUser(user);
                }
                user._id = userGet._id;
                this.mapUser[email] = onlineUsers[email];
                this.markLocaionWithIp(
                  this.mapUser[email].locationInfo,
                  onlineUsers[email].user.email,
                  user
                );
              });
            }
          }
        });
      }, 2000);
    },
    clickMarker(marker) {
      this.chooseUser = marker.user._id;
      this.dialogVisible = true;
      console.log("marker", marker);
      // this.pubsubmessage.sendMessage(
      //   "Message comming",
      //   this.user.name,
      //   marker.name
      // );
    },
    drawMarker(lat, long) {},
    getWidthScreen() {
      return this.screen.width;
    },
    getHeightScreen() {
      return this.screen.height;
    },
    getStyleMap() {
      return `margin: auto;width: ${this.ratioMap *
        this.getWidthScreen()}px; height: ${this.ratioMap *
        this.getHeightScreen()}px`;
    },
    mark(event) {
      this.markers.push({
        id: new Date().getSeconds(),
        position: { lat: event.latLng.lat(), lng: event.latLng.lng() }
      });
    },
    markLocaionWithIp(locationInfo, email, user) {
      const result = locationInfo;
      const marker = new Marker(
        result,
        result.city,
        result.latitude,
        result.longitude,
        email,
        user
      );
      if (email === this.user.email) {
        marker.icon = {
          url: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
        };
      }
      const index = this.markers.findIndex(x => {
        return x.user._id === marker.user._id;
      });
      if (index === -1) {
        this.markers.push(marker);
      } else {
        this.markers[index] = marker;
      }
    },
    handleClose(done) {
      done();
    }
  }
};
</script>
<style >
body,
html {
  padding: 0;
  margin: 0;
  width: 100%;
  min-height: 100%;
}
.el-dialog__body {
  padding: 0px;
}
.full-height {
  padding: 3em;
  margin: auto;
}
.button-light {
  background: #fff;
  border: 1px solid #46484e;
  color: #46484e;
}

.button-dark {
  background: #1c1d21;
  border: 1px solid #1c1d21;
  color: #fff;
}
</style>

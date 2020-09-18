<template>
  <div v-if="screen !== null" class="full-height">
    <el-button style="margin:3em" @click="clickTest()">Test</el-button>

    <el-badge :value="12" class="item">
      <el-dropdown trigger="click" @command="handleCommand">
        <el-button circle type="warning">
          <i class="el-icon-message" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="[key, val] of getmapPubSubMessage"
            :key="key"
            :command="key"
          >
            <el-divider content-position="left">
              <span class="title">{{ val.reciver }}</span>
            </el-divider>
            <span>I cannot choose the best. The best chooses me.</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-badge>
    <!-- <GmapMap
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
        :draggable="true"
        @click="clickMarker(m)"
      />
    </GmapMap> -->
    <div v-for="[k, objectReciver] of mapPubSubMessage" :key="k">
      <!-- v-if="objectReciver!=null && getOtherPeoPle(objectReciver)===currentChatUser" -->
      <div style="margin:50px">
        <BoxChat
          :is-start="objectReciver.reciver != ''"
          :me="user.name"
          :sender="objectReciver.sender"
          :reciver="objectReciver.reciver"
          :pubsubmessage="objectReciver.pubsubmessage"
          :message-reciver="messageReciver"
          @assignReciver="assignReciver"
        />
      </div>
    </div>
    <!-- <BoxChat
      v-if="objectReciver!=null && objectReciver!=0"
      :key="keyReload"
      :is-start="objectReciver.reciver != ''"
      :me="user.name"
      :sender="objectReciver.sender"
      :reciver="objectReciver.reciver"
      :pubsubmessage="objectReciver.pubsubmessage"
      :message-reciver="messageReciver"
      @assignReciver="assignReciver"
    />-->
  </div>
</template>

<script>
class Marker {
  constructor(info, city, lat, lng, name) {
    this.id = city;
    this.info = info;
    this.position = {
      lat: lat,
      lng: lng
    };
    this.name = name;
    // this.icon = "https://img.icons8.com/bubbles/50/000000/street-view.png";
  }
}
import { getInfoFromExtApi, getInfoWithIp } from "@/api/apiExt";
import { getOtherPeoPle } from "@/api/apiInfo";
import store from "@/store";
import PubSubMessageClass from "@/utils/pubsubmessage";
export default {
  components: {
    BoxChat: () => import("@/components/BoxChat")
  },
  data() {
    return {
      ratioMap: 0.7,
      screen: null,
      markers: [],
      zoom: 2,
      centerMap: { lat: 0, lng: 0 },
      message: "test",
      mapUser: {},
      reciver: "",
      mapPubSubMessage: new Map(),
      mapPubSubMessageTracker: 0,
      messageReciver: null,
      currentChatUser: null,
      keyReload: 0
    };
  },
  computed: {
    mySetAsList() {
      return this.mySetChangeTracker && this.mapPubSubMessage;
    },
    getmapPubSubMessage() {
      this.mapPubSubMessageTracker;

      return this.mapPubSubMessageTracker && this.mapPubSubMessage;
    },

    user() {
      return this.$store.state.user.user;
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
    // objectReciver() {
    //   console.log(this.reciver, "recuver");
    //   if (this.reciver !== "") {
    //     console.log("objectReciver", this.getOnemapPubSubMessage(this.reciver));
    //     return (
    //       this.mapPubSubMessageTracker &&
    //       this.getOnemapPubSubMessage(this.reciver)
    //     );
    //   }
    //   console.log(
    //     " this.mapPubSubMessageTracker && null",
    //     this.mapPubSubMessageTracker && null
    //   );
    //   return this.mapPubSubMessageTracker && null;
    // }
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
    }
  },
  mounted() {
    this.screen = screen;
    this.markLocaionWithIp(this.user.ip, this.user.name);
    this.timmerGetInfo();
    const pubsubmessage = new PubSubMessageClass(
      this.user.name,
      this.onMessageReceived,
      true
    );
    this.mapPubSubMessage.set(this.user.name, {
      pubsubmessage: pubsubmessage
    });
    // pubsubmessage.subcibetopic(this.user.name);
  },
  methods: {
    // getClass(objectReciver) {
    //   if (this.getOtherPeoPle(objectReciver) !== this.currentChatUser) {
    //     console.log("hidden", objectReciver);

    //     return "margin-right:10em;visibility: hidden;";
    //   }
    //   console.log(objectReciver);
    // },
    getOtherPeoPle(objectReciver) {
      if (objectReciver.sender === this.user.name) {
        return objectReciver.reciver;
      } else {
        return objectReciver.sender;
      }
    },
    onMessageReceived(payload) {
      const payloadReciver = JSON.parse(payload.body);
      const reciver = payloadReciver.reciver;
      const objReciver = this.mapPubSubMessage.get(reciver);
      console.log(
        "this.currentChatUser",
        this.currentChatUser,
        payloadReciver.sender
      );
      if (this.currentChatUser !== payloadReciver.sender) {
        this.currentChatUser = payloadReciver.sender;
        this.keyReload++;
      }
      if (objReciver !== null) {
        this.messageReciver = payloadReciver;
        const name = reciver;
        this.reciver = name;
        const pubsubmessage = new PubSubMessageClass(
          name,
          this.onMessageReceived
        );
        this.mapPubSubMessage.set(name, {
          pubsubmessage: pubsubmessage,
          reciver: payloadReciver.reciver,
          sender: payloadReciver.sender
        });
        this.mapPubSubMessageTracker++;
      } else {
        this.messageReciver = Object.assign(
          {},
          this.messageReciver,
          payloadReciver
        );
      }
    },
    handleCommand(command) {
      console.log("command:", command);

      this.chooseReciver(command);
    },
    chooseReciver(key) {
      console.log("key:", key);
      this.reciver = key;
    },
    getOnemapPubSubMessage(name) {
      for (const [key, value] of this.mapPubSubMessage) {
        if (key === name) {
          return value;
        }
      }
      return this.mapPubSubMessageTracker && null;
    },
    clickTest() {
      const reciver = "reciver" + new Date().getSeconds().toString();
      const name = this.user.name + "_" + reciver;
      const pubsubmessage = new PubSubMessageClass(
        name,
        this.onMessageReceived
      );
      this.mapPubSubMessage.set(name, {
        pubsubmessage: pubsubmessage,
        reciver: reciver
      });
      this.mapPubSubMessageTracker++;
    },
    assignReciver(reciverAssign) {
      this.reciver = reciverAssign;
    },
    timmerGetInfo() {
      setInterval(() => {
        getOtherPeoPle().then(resp => {
          if (resp.error === 0) {
            const userofapp = resp.data.userofapp;
            for (const name of Object.keys(userofapp)) {
              if (name !== this.user.name) {
                if (this.mapUser[name] === undefined) {
                  this.mapUser[name] = userofapp[name];
                  this.markLocaionWithIp(
                    userofapp[name].ip,
                    userofapp[name].name
                  );
                }
              }
            }
          }
        });
      }, 5000);
    },
    clickMarker(marker) {
      this.centerMap = marker.position;
      this.message = JSON.stringify(marker);
      const name = marker.name;
      this.reciver = name;
      this.currentChatUser = this.reciver;
      const pubsubmessage = new PubSubMessageClass(
        name,
        this.onMessageReceived
      );
      this.mapPubSubMessage.set(name, {
        pubsubmessage: pubsubmessage,
        reciver: this.reciver,
        sender: this.user.name
      });
      this.mapPubSubMessageTracker++;
    },
    drawMarker(lat, long) {
      this.markers.push;
    },
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
    markLocaionWithIp(ip, name) {
      getInfoWithIp(ip).then(resp => {
        const result = resp.data;

        const marker = new Marker(
          result,
          result.city,
          result.latitude,
          result.longitude,
          name
        );
        if (name === this.user.name) {
          marker.icon = {
            url: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
          };
        }
        this.markers.push(marker);
      });
    }
  }
};
</script>
<style>
body,
html {
  padding: 0;
  margin: 0;
  width: 100%;
  min-height: 100%;
}
.full-height {
  padding: 3em;
  margin: auto;
}
</style>

<template>
  <div>
    <el-button type="text" @click="dialogVisible = true">click to open the Dialog</el-button>

    <el-dialog title="Chat" :visible.sync="dialogVisible" width="50%" :before-close="handleClose">
      <Chat invited-user-id="bb" :users="users" :current-user-id="user.name" />
    </el-dialog>
  </div>
</template>

<script>
import UserChat from "@/utils/UserChat";
export default {
  components: {
    Chat: () => import("@/components/Chat")
  },
  data() {
    return {
      users: [],
      dialogVisible: false
    };
  },
  computed: {
    user() {
      return this.$store.state.user.user;
    }
  },
  mounted() {
    console.log("pushUser", this.user);
    this.users.push(
      new UserChat(this.user.name, this.user.name, this.user.avatar)
    );
  },
  methods: {
    handleClose(done) {
      done();

      //   this.$confirm("Are you sure to close this dialog?")
      //     .then(_ => {
      //       done();
      //     })
      //     .catch(_ => {});
    }
  }
};
</script>

<style></style>

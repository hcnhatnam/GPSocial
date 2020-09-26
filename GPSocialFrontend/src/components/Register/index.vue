<template>
  <div class="container">
    <el-form ref="form" :model="form" label-width="100px">
      <el-form-item label="Email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="User Name">
          <el-input v-model="form.username" />
        </el-form-item>

        <!-- <el-form-item label="Gender">
        <el-radio-group v-model="form.gender">
          <el-radio label="Male" />
          <el-radio label="FeMale" />
        </el-radio-group>
        </el-form-item>-->

        <!-- <el-form-item label="Birth Day">
        <el-date-picker
          v-model="form.birthdate"
          type="date"
          placeholder="Pick a birth date"
          style="width: 100%;"
          :clearable="false"
        />
        </el-form-item>-->
        <el-form-item label="Avatar">
          <el-input
            v-model="form.profilepiclink"
            style="width:100%;border:1px solid;border-radius:0.5em"
          />
          <el-card>
            <img
              :src="form.profilepiclink==''?defaultfAvatar:form.profilepiclink"
              style="width:9em"
            />
          </el-card>
        </el-form-item>
        <el-form-item label="Location">
          <i style="font-size:1.5em" class="el-icon-map-location" />
          <span>{{ form.location }}</span>
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" />
        </el-form-item>
      </el-form>
      <div style="display: grid;">
        <el-button type="primary" @click="onSubmit">Register</el-button>
      </div>
    </el-form>
  </div>
</template>
<script>
import { getInfoWithIp2location, getInfoLocation } from "@/api/apiInfo";
import { registerUser } from "@/api/user";
import user from "../../store/modules/user";
export default {
  data() {
    return {
      defaultfAvatar:
        "https://graceumcseattle.org/wp-content/uploads/2016/12/headshot-400.png",
      form: {
        username: "",
        email: "",
        location: "",
        profilepiclink: "",
        password: ""
      }
    };
  },
  mounted() {
    getInfoLocation().then(resp => {
      if (resp.error === 0) {
        const data = resp.data;
        this.form.location = data.info.city + ", " + data.info.country_name;
      }
      console.log(resp);
      // getInfoWithIp2location();
    });
  },
  methods: {
    notiFail(message) {
      this.$notify.error({
        title: "Fail",
        message: message
      });
    },
    notiSuccess(message) {
      this.$notify({
        title: "Success",
        message: message,
        type: "success"
      });
    },
    onSubmit() {
      const { email, username, profilepiclink, password } = this.form;
      if (email === "") {
        this.notiFail("email empty!");
      } else if (username === "") {
        this.notiFail("username empty!");
      } else if (password === "") {
        this.notiFail("password empty!");
      } else {
        let profilepiclinkChoose = profilepiclink;
        if (profilepiclink === "") {
          profilepiclinkChoose = this.defaultfAvatar;
        }
        registerUser(
          email,
          username,
          profilepiclink,
          profilepiclinkChoose
        ).then(resp => {
          if (resp.error === 0) {
            this.notiSuccess(`Register user ${email} success!!!`);
            this.$emit("close", this.form);
          } else {
            this.notiFail(resp.message);
          }
        });
      }
    },
    Login() {
      this.$router.push({ path: "login" });
    }
  }
};
</script>
<style scoped>
#title {
  font-size: 2em;
  font-weight: bold;
}
</style>

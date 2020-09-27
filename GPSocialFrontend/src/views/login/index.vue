<template>
  <div class="login-container">
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      auto-complete="on"
      label-position="left"
    >
      <div class="title-container">
        <h3 class="title">Login Form</h3>
      </div>

      <el-form-item prop="email">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="email"
          v-model="loginForm.email"
          placeholder="Email"
          name="email"
          type="text"
          tabindex="1"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="Password"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-button
        :loading="loading"
        type="primary"
        style="width:100%;margin-bottom:30px;"
        @click.native.prevent="handleLogin"
      >Login</el-button>

      <div class="tips" @click="dialogVisible=true">
        <div>
          <el-link icon="el-icon-edit" type="primary">Register user?</el-link>
        </div>
        <!-- <span style="margin-right:20px;">username: admin</span>
        <span>password: any</span>-->
      </div>
    </el-form>
    <p class="copyright">
      © GPSocial TEAM
      <br />
      <i class="el-icon-star-on" />
      <a
        target="_blank"
        href="https://github.com/hcnhatnam/GPSocial"
      >https://github.com/hcnhatnam/GPSocial</a>
    </p>
    <el-dialog
      title="Register"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <Register @close="handleClose" />
    </el-dialog>
  </div>
</template>

<script>
import { validUsername } from "@/utils/validate";
import { findUser } from "@/firestore/index";
export default {
  name: "Login",
  components: {
    Register: () => import("@/components/Register")
  },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error("Please enter the correct user name"));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (value.length < 1) {
        callback(new Error("The password can not be less than 1 digits"));
      } else {
        callback();
      }
    };
    return {
      dialogVisible: false,
      loginForm: {
        email: "admin",
        password: "111111"
      },
      loginRules: {
        email: [
          { required: true, trigger: "blur", validator: validateUsername }
        ],
        password: [
          { required: true, trigger: "blur", validator: validatePassword }
        ]
      },
      loading: false,
      passwordType: "password",
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    register() {
      console.log("regster");
      // const appName = rowTable.appname;
      // const params = {
      //   appname: appName,
      //   name: this.aliasname(appName),
      //   nametype: this.nametype
      // };
      this.$router.push({
        name: "Register"
        // name: "AppOwnerDetail",
        // params: params
      });
    },
    showPwd() {
      if (this.passwordType === "password") {
        this.passwordType = "";
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    handleLogin() {
      console.log("handleLogin");
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$store
            .dispatch("user/login", this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || "/" });
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    }
  }
};
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg: #283443;
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  footer {
    position: relative;
    height: 300px;
    width: 100%;
    background-color: #333333;
  }

  p.copyright {
    position: absolute;
    width: 100%;
    color: #000;
    line-height: 3em;
    font-size: 0.7em;
    text-align: center;
    bottom: 0;
    font-size: 1.5em;
    font-weight: 900;
  }
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;
    min-height: 1.5em;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: #000;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg: url("https://f5-group-zf.zdn.vn/2711ef554d17a249fb06/2543873291198976094");
$dark_gray: #889aa4;
$light_gray: black;

.login-container {
  min-height: 100%;
  width: 100%;
  background-image: $bg;
  background-repeat: no-repeat;
  overflow: hidden;
  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 16em 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 40px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>

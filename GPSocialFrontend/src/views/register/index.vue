<template>
  <div class="container">
    <el-card shadow="always">
      <div slot="header" class="clearfix">
        <span id="title">Register</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="Login()">Login</el-button>
      </div>

      <el-form ref="form" :model="form" label-width="100px">
        <el-col :span="11">
          <el-form-item label="User Name">
            <el-input v-model="form.name" />
          </el-form-item>
        </el-col>

        <el-col :span="11">
          <el-form-item label="Gender">
            <el-radio-group v-model="form.gender">
              <el-radio label="Male" />
              <el-radio label="FeMale" />
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item label="Email">
            <el-input v-model="form.email" />
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item label="Birth Day">
            <el-date-picker
              v-model="form.birthdate"
              type="date"
              placeholder="Pick a birth date"
              style="width: 100%;"
              :clearable="false"
            />
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item label="Avatar">
            <v-gravatar :email="form.email" :alt="form.email" :size="180" default-img="mm" />
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item label="Location">
            <i style="font-size:1.5em" class="el-icon-map-location" />
            <span>{{ form.location }}</span>
          </el-form-item>
        </el-col>

        <el-col :span="11">
          <el-form-item label-width="5em">
            <div>
              <el-button type="primary" @click="onSubmit">Register</el-button>
              <el-button>Cancel</el-button>
            </div>
          </el-form-item>
        </el-col>
      </el-form>
    </el-card>
  </div>
</template>
<script>
import { getInfoWithIp2location } from "@/api/apiInfo";
export default {
  data() {
    return {
      form: {
        name: "",
        birthdate: Date.now(),
        type: [],
        gender: "Male",
        email: "",
        location: ""
      }
    };
  },
  mounted() {
    // getIp().then(resp => {
    //   console.log(resp);
    //   // getInfoWithIp2location();
    // });
  },
  methods: {
    onSubmit() {
      console.log("submit!", this.form);
    },
    Login() {
      this.$router.push({ path: "login" });
    }
  }
};
</script>
<style scoped>
.container {
  width: 35%;
  margin: 20vh auto;
}
#title {
  font-size: 2em;
  font-weight: bold;
}
</style>

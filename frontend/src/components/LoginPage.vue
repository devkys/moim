<script setup>
import { useField, useForm } from 'vee-validate'
import axios from "axios";
import router from "@/router";
import {useUserStore} from "@/store/user";

// 피니아 상태(data)관리 라이브러리
const store = useUserStore();

const { handleSubmit } = useForm({
  validationSchema: {
    email (value) {
      if (value) return true
      return '이메일을 입력해주세요'
    },
    pw (value) {
      if(value) return true
      return '비밀번호를 입력 해주세요'
    },
  },
})

const email = useField('email')
const pw = useField('pw')

const submit = handleSubmit(values => {
  axios({
    method: 'post',
    headers: {'Content-Type' : 'application/json'},
    url: 'api/users-mgmt/login',
    data: JSON.stringify(values, null)
  }).then(function (res) {
    if(res.data) {
      // login DTO 정보를 응답으로 받음

      // 스토어 상태 변경
      store.$patch({
        email : res.data.email,
        nickname: res.data.nickname,
        access_token: res.data.access_token,
        codeValue: res.data.codeValue
      })

      // router.push({name: 'main', state: { user_info: res.data }})
      router.push('/main')

    }
  }).catch(function (error) {
    if(error.response.data.code==="ACCOUNT-001") {
      alert(error.response.data.message);
    }
  })
})

</script>

<template>
  <h1>로그인</h1>
  <form @submit.prevent="submit">
    <v-text-field
        v-model="email.value.value"
        :error-messages="email.errorMessage.value"
        label="이메일"
    ></v-text-field>

    <v-text-field
        v-model="pw.value.value"
        type="password"
        :counter="20"
        :error-messages="pw.errorMessage.value"
        label="비밀번호"
    ></v-text-field>

    <div class="btn_div">
      <v-btn
          class="me-4"
          type="submit"
      >
        submit
      </v-btn>

    </div>
  </form>
  <router-link to="register" class="reg_link">회원이 아니신가요?</router-link>
</template>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1 {
  margin-top: 100px;
  margin-bottom: 50px;
  text-align: center;
}
form {
  width: 50%;
  margin: 0 auto;
}
.btn_div {
  text-align: center;
}
.reg_link {
  margin-top: 30px;
  text-decoration: none;
  color: black;
  display: flex;
  justify-content: center;
  text-align: center;
  &:hover {
    font-weight: bold;
  }
}
</style>

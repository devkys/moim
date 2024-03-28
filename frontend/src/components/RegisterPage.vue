<script setup>
// import { ref} from 'vue'
import {useField, useForm} from 'vee-validate'
import axios from "axios";
import router from "@/router";

const {handleSubmit, handleReset} = useForm({
  validationSchema: {
    nickname(value) {
      if (value?.length >= 2) return true

      return '2글자 이상 입력해주세요.'
    },
    email(value) {
      if (/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(value)) return true

      return '유효한 이메일 형식을 입력해주세요.'
    },
    pw(value) {
      if (/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/.test(value)) return true

      return '영문 숫자 특수기호 조합 8자리 이상 입력해주세요.'
    },
    pw_confirm(value) {
      if (pw.value.value === value) return true
      return '비밀번호가 일치하지 않습니다.'
    },
  },
})

const email = useField('email')
const pw = useField('pw')
const pw_confirm = useField('pw_confirm')
const nickname = useField('nickname')

const submit = handleSubmit(values => {
  // alert(JSON.stringify(values, null, 2))
  axios.post('api/users-mgmt/signup', values)
      .then(() => {
        alert("회원 가입 성공! 로그인 페이지로 이동합니다.");
        router.push({name: 'login'})
      })
      .catch((error) => {
        if (error.response) {
          alert(error.response.data.message);
        }
      })
})

</script>

<template>
  <h1>회원 가입</h1>
  <form @submit.prevent="submit">
    <v-text-field
        v-model="email.value.value"
        :error-messages="email.errorMessage.value"
        label="이메일"
    ></v-text-field>

    <v-text-field
        v-model="nickname.value.value"
        :counter="10"
        :error-messages="nickname.errorMessage.value"
        label="닉네임"
    ></v-text-field>

    <v-text-field
        v-model="pw.value.value"
        type="password"
        :counter="20"
        :error-messages="pw.errorMessage.value"
        label="비밀번호"
    ></v-text-field>

    <v-text-field
        v-model="pw_confirm.value.value"
        type="password"
        :counter="20"
        :error-messages="pw_confirm.errorMessage.value"
        label="비밀번호 확인"
    >
    </v-text-field>

    <div class="btn_div">
      <v-btn
          class="me-4"
          type="submit"
      >
        submit
      </v-btn>

      <v-btn @click="handleReset">
        clear
      </v-btn>
    </div>

  </form>
  <router-link to="login" class="login_link">이미 회원이신가요?</router-link>
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

.login_link {
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

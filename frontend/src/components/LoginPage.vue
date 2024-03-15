<script setup>
// import { ref} from 'vue'
import { useField, useForm } from 'vee-validate'
import axios from "axios";

const { handleSubmit } = useForm({
  validationSchema: {
    email (value) {
      if (/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(value)) return true

      return '유효한 이메일 형식을 입력해주세요.'
    },
    pw (value) {
      if(/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/.test(value)) return true

      return '영문 숫자 특수기호 조합 8자리 이상 입력해주세요.'
    },

  },
})

const email = useField('email')
const pw = useField('pw')

const submit = handleSubmit(values => {
  alert(JSON.stringify(values, null, 2))
  axios.post('api/users-mgmt/signin', values).then(res => console.log(res.data)).catch(err => console.log(err))
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

</style>

<script setup>
// import { ref} from 'vue'
import { useField, useForm } from 'vee-validate'
import axios from "axios";
import router from "@/router";

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
    url: 'api/users-mgmt/signin',
    data: JSON.stringify(values, null)
  }).then(function (res) {
    if(res.data) {
      router.push({name: 'main'})
    }
  }).catch((e) => console.log(`${e.error}`))
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

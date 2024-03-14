<script setup>
// import { ref} from 'vue'
import { useField, useForm } from 'vee-validate'
import axios from "axios";

// const get_users = () => {
//   axios({
//     url: "/api/users-mgmt/signin",
//     method: "get",
//     timeout: 5000,
//   }).then((response) => {
//     if (response.status === 200) {
//       console.log("responset!!!!!" + response.data);
//     }
//   }).catch((e) => {
//     console.log(`${e.name}(${e.code} : ${e.message})`);
//     if (e.code === 'ECONNABORTED') {
//       console.log('페이지를 새로고침하세요');
//     }
//   });
// };


const { handleSubmit, handleReset } = useForm({
  validationSchema: {
    nickname (value) {
      if (value?.length >= 2) return true

      return '2글자 이상 입력해주세요.'
    },
    // phone (value) {
    //   if (value?.length > 9 && /[0-9-]+/.test(value)) return true
    //
    //   return 'Phone number needs to be at least 9 digits.'
    // },
    email (value) {
      if (/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(value)) return true

      return '유효한 이메일 형식을 입력해주세요.'
    },
    pw (value) {
      if(/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/.test(value)) return true

      return '영문 숫자 특수기호 조합 8자리 이상 입력해주세요.'
    },
    pw_confirm(value) {
      if(pw.value.value === value) return true
      return '비밀번호가 일치하지 않습니다.'
    },
    // select (value) {
    //   if (value) return true
    //
    //   return 'Select an item.'
    // },
    // checkbox (value) {
    //   if (value === '1') return true
    //
    //   return 'Must be checked.'
    // },
  },
})

const email = useField('email')
const pw = useField('pw')
const pw_confirm = useField('pw_confirm')
const nickname = useField('nickname')
// const select = useField('select')
// const checkbox = useField('checkbox')

// const items = ref([
//   'Item 1',
//   'Item 2',
//   'Item 3',
//   'Item 4',
// ])

const submit = handleSubmit(values => {
  alert(JSON.stringify(values, null, 2))
  axios.post('api/users-mgmt/signup', values).then(res => console.log(res)).catch(err => console.log(err))

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



<!--    <v-select-->
<!--        v-model="select.value.value"-->
<!--        :error-messages="select.errorMessage.value"-->
<!--        :items="items"-->
<!--        label="Select"-->
<!--    ></v-select>-->

<!--    <v-checkbox-->
<!--        v-model="checkbox.value.value"-->
<!--        :error-messages="checkbox.errorMessage.value"-->
<!--        label="Option"-->
<!--        type="checkbox"-->
<!--        value="1"-->
<!--    ></v-checkbox>-->

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

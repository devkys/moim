<script setup>
import { onMounted } from "vue";
import axios from "axios";
import {ref} from "vue";
import {useClipboard} from '@vueuse/core';
const schedule_list = ref();
const invite_list = ref();
const add_dialog=ref(false);
const invite_modal = ref(false);
const { copy } = useClipboard();
const invite_url="http://localhost:8081/api/sch_mgmt/invite-sch/"

const { user_info } = history.state;

// 로그인한 유저가 직접 생성한 일정
function getMy() {
  return axios.get('api/sch_mgmt/main-board?email=' + user_info.email);
}

// 다른 유저로부터 초대받은 일정
function getInvited() {
  return axios.get('api/sch_mgmt/invite-board?email=' + user_info.email);
}

// 세션을 조회하여 초대 여부 확인
function whetherIvite() {
  return axios.get('api/sch_mgmt/check-invite?email=' + user_info.email);
}

// 컴포넌트가 마운트된 후 호출 될 콜백 함수
onMounted(() => {
  // 내 일정, 초대 일정 axios all로 멀티 요청
  axios.all([getMy(), getInvited(), whetherIvite()])
      .then(axios.spread(function (my, invited, whether) {
        schedule_list.value = my.data;
        invite_list.value = invited.data;

        // 초대된 링크 여부 확인
        if(whether.data.toString() === "true") {
          invite_modal.value = true;
        }

      }))
      .catch((e)=>console.log(`${e.error} : ${e.message}`));
})


// 초대받은 세션이 존재하면 활성화되는 함수
// 초대 수락 or 거절
function inviteAgree(e) {
  axios({
    method: 'post',
    headers: {'Content-Type' : 'application/json'},
    url: 'api/room_mgmt/agree-invite',
    data: {
      choose: e,
      email: user_info.email
    }
  }).then((res) => {
    console.log(res.data);

  }).catch((e) => console.log(`${e.error}`))
}

</script>

<template>

  <h2>{{user_info.nickname}}</h2>

<!--  초대 수락 or 거절 modal -->
  <v-dialog
      v-model="invite_modal"
      max-width="400"
      persistent
  >
    <v-card
        prepend-icon="mdi-email-fast-outline"
        text="초대를 수락하시겠습니까"
        title="유효한 초대"
    >
      <template v-slot:actions>
        <v-spacer></v-spacer>
        <v-btn @click="invite_modal = false; inviteAgree(false)">
          거절
        </v-btn>

        <v-btn @click="invite_modal = false; inviteAgree(true) ">
          수락
        </v-btn>
      </template>
    </v-card>
  </v-dialog>

  <div class="list_div">
    <v-expansion-panels  variant="popout" >
      <v-expansion-panel
          v-for="schedule in schedule_list"
          :key="schedule"
          class="schedule_list"
          icon="mdi-expand"
      >
        <v-expansion-panel-title>
          <h3> {{schedule.title }}</h3>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <strong> {{schedule.content}} </strong> <br>
          <span> 일정 </span>
          <span> {{ schedule.duedate }}</span>
          <span> 장소 </span>
          <span> {{schedule.place }}</span>
          <span>{{schedule.seq}}</span>
          <v-btn @click="copy(invite_url+schedule.seq)">초대링크</v-btn>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

  <div class="invite_div">
    <v-expansion-panels  variant="popout" >
      <v-expansion-panel
          v-for="schedule in invite_list"
          :key="schedule"
          class="schedule_list"
          icon="mdi-expand"
      >
        <v-expansion-panel-title>
          <h3> {{schedule.title }}</h3>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <strong> {{schedule.content}} </strong> <br>
          <span> 일정 </span>
          <span> {{ schedule.duedate }}</span>
          <span> 장소 </span>
          <span> {{schedule.place }}</span>
          <span>{{schedule.seq}}</span>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

  <v-col cols="auto">
    <v-btn icon="mdi-plus" fab absolute bottom elevation="11" @click="add_dialog = true"></v-btn>
  </v-col>

  <v-dialog
      v-model="add_dialog"
      width="auto"
  >
    <v-card
        max-width="800"
        prepend-icon="mdi-plus"
        text="Your application will relaunch automatically after the update is complete."
        title="일정 추가하기"
    >
      <template v-slot:actions>
        <v-btn
            class="ms-auto"
            text="Ok"
            @click="add_dialog = false"
        ></v-btn>
      </template>
    </v-card>
  </v-dialog>

</template>
<style scoped>

.list_div {
  width: 70%;
  margin: 0 auto;
}

.schedule_list {
  margin: 0 auto;
  background-color: #F5F7FA;
  width: 70%;
  padding: 8px 0px 10px 0px;
  border-radius: 8px;
}

#map {
  width: 300px;
  height: 300px;
}
</style>
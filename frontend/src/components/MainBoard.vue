<script setup>
import { onMounted } from "vue";
import axios from "axios";
import {ref} from "vue";
import {useClipboard} from '@vueuse/core';
const schedule_list = ref();
const add_dialog=ref(false);
const snackbar = ref(false);
const invite_modal = ref(false);
const { copy } = useClipboard();
const invite_url="http://localhost:8081/api/sch_mgmt/invite-sch/"

const { email } = history.state;

onMounted(() => {
  axios({
    method: 'get',
    url: 'api/sch_mgmt/main-board?email=' + email,
  }).then((response) => {
    schedule_list.value = response.data;
    console.log(schedule_list);
  }).catch((e) => {
    console.log(`${e.name}(${e.code} : ${e.message})`);
  })

  axios({
    method: 'get',
    url: 'api/sch_mgmt/check-invite'
  }).then((res) => {
    if(res.data.isEmpty) {
      console.log("nothing invite");
    }
    else {
      snackbar.value = true;
    }
  }).catch((e) => {
    console.log(`${e.name}(${e.code} : ${e.message})`);
  })
})

function inviteAgree(e) {
  axios({
    method: 'post',
    headers: {'Content-Type' : 'application/json'},
    url: 'api/room_mgmt/agree-invite',
    data: {
      choose: e,
      email: email
    }
  }).then((res) => {
    alert("invite");
    console.log(res.data);

  }).catch((e) => console.log(`${e.error}`))
}

</script>

<template>

  <h2>r</h2>

  <v-dialog
      v-model="invite_modal"
      max-width="400"
      persistent
  >
    <template v-slot:activator="{ props: activatorProps }">
      <v-btn
          v-if="snackbar = true"
          v-bind="activatorProps"
          color="primary"
          variant="text"
          @click="invite_modal = true"
      >
        초대장 보기
      </v-btn>
    </template>
    <v-card
        prepend-icon="mdi-email-fast-outline"
        text="초대를 수락하시겠습니까"
        title="초대장"
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



  <v-snackbar
      v-model="snackbar"
      vertical
      top
  >
    <div class="invite">
      유효한 초대장이 있습니다.
    </div>

    <template v-slot:actions>
      <v-btn
          color="primary"
          variant="text"
          @click="snackbar = false"
      >
        닫기
      </v-btn>
    </template>
  </v-snackbar>

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
            @click="dialog = false"
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
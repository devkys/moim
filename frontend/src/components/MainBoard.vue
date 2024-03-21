<script setup>
import {onMounted} from "vue";
import axios from "axios";
import {ref} from "vue";
import {useClipboard, useDateFormat} from '@vueuse/core';
import {useField, useForm} from "vee-validate";
import router from "@/router";

const schedule_list = ref();
const invite_list = ref();
const add_dialog = ref(false);
const invite_modal = ref(false);
const {copy} = useClipboard();
const invite_url = "http://localhost:8081/api/sch_mgmt/invite-sch/"
const drawer = ref(null);
const sch_info = ref([]);
const message = "";
const {user_info} = history.state;
// const user_email = history.state.email;

const ws = new WebSocket("ws://localhost:8081");

ws.onopen = function(event) {
  ws.send("Client Messsage");
  console.log(event);
}

ws.onmessage = function(event) {
  console.log("SErver MEssage" , event.data);
}

ws.onerror = function(event) {
  console.log("Server error Message : ", event.data);
}

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

function deleteSchedule(s_id) {
  alert(s_id)
  axios.post('api/sch_mgmt/deleteOne', s_id)
      .then((res) => {
        if (res.data.value === 1) {
          alert("삭제 완!!!!!!!!!!!!");
          router.go(0);
        }
      })
      .catch((e) => console.log(`${e.error} : ${e.message}`))
}

const {handleSubmit} = useForm({
  validationSchema: {
    title(value) {
      if (value?.length <= 20) return true

      return '10자 이하로 입력해주세요.'
    },
    content(value) {
      if (value?.length <= 30) return true

      return '30자 이하로 입력해주세요.'
    },
    place(value) {
      if (value?.length <= 50) return true

      return '50자 이하로 입력해주세요,';
    },
  }
})

const title = useField('title');
const content = useField('content');
const place = useField('place');
const duedate = useField('duedate');

const scheduleSave = handleSubmit(values => {
  values.email = user_info.email;
  axios.post('api/sch_mgmt/save', values)
      .then(res => {
        if (res.data) {
          router.go(0);
        } else {
          alert('error 발생');
        }
      })
      .catch(err => console.log(err))
})


// 컴포넌트가 마운트된 후 호출 될 콜백 함수
onMounted(() => {
  alert(window.location.host);

  // 내 일정, 초대 일정 axios all로 멀티 요청
  axios.all([getMy(), getInvited(), whetherIvite()])
      .then(axios.spread(function (my, invited, whether) {
        schedule_list.value = my.data;
        invite_list.value = invited.data;

        // 초대된 링크 여부 확인
        if (whether.data.toString() === "true") {
          invite_modal.value = true;
        }

      }))
      .catch((e) => console.log(`${e.error} : ${e.message}`));
})

// 초대받은 세션이 존재하면 활성화되는 함수
// 초대 수락 or 거절
function inviteAgree(e) {
  axios({
    method: 'post',
    headers: {'Content-Type': 'application/json'},
    url: 'api/room_mgmt/agree-invite',
    data: {
      choose: e,
      email: user_info.email
    }
  }).then((res) => {
    console.log(res.data);
    router.go(0);
  }).catch((e) => console.log(`${e.error}`))
}

</script>

<template>
  <div>

  </div>
  <h2>{{ user_info.nickname }}님의 일정</h2>

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
    <v-expansion-panels variant="popout">
      <v-expansion-panel
          v-for="schedule in schedule_list"
          :key="schedule"
          class="schedule_list"
          icon="mdi-expand"
      >
        <v-expansion-panel-title>
          <h3> {{ schedule.title }}</h3>
          <v-hover v-slot="{ isHovering, props }">
            <v-icon
                class="trash_can"
                v-bind="props"
                :class="{'on-hover' : isHovering}"
                icon="mdi-trash-can"
                @click="deleteSchedule(schedule.seq)"
            ></v-icon>

            <div class="justify-center align-center">
              <v-icon
                  v-bind="props"
                  :class="{'on-hover' : isHovering}"
                  icon="mdi-message-text-outline"
                  @click.stop="drawer = !drawer"
                  v-on:click="sch_info.seq = schedule.seq"
              ></v-icon>
            </div>
          </v-hover>
        </v-expansion-panel-title>

        <v-expansion-panel-text>
          <strong> {{ schedule.content }} </strong> <br>
          <span> 일정 </span>
          <span> {{ useDateFormat(schedule.duedate, 'YYYY-MM-DD HH:mm:ss') }}</span>
          <span> 장소 </span>
          <span> {{ schedule.place }}</span>
          <span>{{ schedule.seq }}</span>
          <v-btn @click="copy(invite_url+schedule.seq)">초대링크</v-btn>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

  <div class="invite_div">
    <v-expansion-panels variant="popout">
      <v-expansion-panel
          v-for="schedule in invite_list"
          :key="schedule"
          class="schedule_list"
          icon="mdi-expand"
      >
        <v-expansion-panel-title>
          <h3> {{ schedule.title }}</h3>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <strong> {{ schedule.content }} </strong> <br>
          <span> 일정 </span>
          <span> {{ useDateFormat(schedule.duedate, 'YYYY-MM-DD HH:mm:ss') }}</span>
          <span> 장소 </span>
          <span> {{ schedule.place }}</span>
          <span>{{ schedule.seq }}</span>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

  <v-dialog
      v-model="add_dialog"
      width="auto"
  >
    <template v-slot:activator="{ props: activatorProps}">
      <v-col cols="auto">
        <v-btn icon="mdi-plus" fab bottom right elevation="11" @click="add_dialog = true"
               v-bind="activatorProps"></v-btn>
      </v-col>
    </template>

    <v-card
        width="800"
        prepend-icon="mdi-plus"
        title="일정 추가하기"
    >
      <v-card-text>
        <form @submit.prevent="scheduleSave">
          <v-row dense>
            <v-col>
              <v-text-field
                  required
                  label="일정 제목"
                  v-model="title.value.value"
                  :error-messages="title.errorMessage.value"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-row dense>
            <v-col>
              <v-text-field
                  v-model="content.value.value"
                  label="일정 내용"
                  :error-messages="content.errorMessage.value"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-row dense>
            <v-col>
              <v-text-field
                  label="장소"
                  v-model="place.value.value"
                  :error-messages="place.errorMessage.value"
              ></v-text-field>
            </v-col>
            <input type="datetime-local"
                   v-model="duedate.value.value"
            >
          </v-row>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                text="닫기"
                variant="plain"
                @click="add_dialog = false"
            ></v-btn>

            <v-btn
                color="primary"
                type="submit"
                text="저장"
                variant="tonal"
            ></v-btn>
          </v-card-actions>

        </form>
      </v-card-text>
    </v-card>
  </v-dialog>

  <v-layout>
    <v-navigation-drawer
        v-model="drawer"
        temporary
        width="450"
    >
      <v-list-item>{{sch_info.seq}} Chat</v-list-item>
      <v-divider></v-divider>

      <v-list
          height="800"
          overflow-y="auto"
          class="chat_div"
      >

<!--        말풍선 div-->
      </v-list>
      <v-divider></v-divider>

      <v-list>
        <v-form>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="message"
                    :append-icon="'mdi-send'"
                    label="채팅 메시지"
                    type="text"
                    variant="filled"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-form>
      </v-list>
    </v-navigation-drawer>
  </v-layout>


</template>
<style scoped>

h2 {
  text-align: center;
}

.list_div {
  width: 50%;
  margin: 0 auto;
  margin-bottom: 100px;
}

.invite_div {
  width: 50%;
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

.on-hover {
  background-color: lightgray;
  border-radius: 50%;
}

.trash_can {
  margin-right: 50px;
  padding: 15px;
}
</style>
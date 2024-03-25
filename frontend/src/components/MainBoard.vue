<script setup>
import axios from "axios";
import DataView from 'primevue/dataview`'
import {reactive, ref, watch} from "vue";
import {now, useClipboard, useDateFormat} from '@vueuse/core';
import { useField, useForm} from "vee-validate";
import router from "@/router";
import webstomp from "webstomp-client";
import SockJS from "sockjs-client";
import {useDropzone} from "vue3-dropzone";
import Image from 'primevue/image';
const schedule_list = ref();
const invite_list = ref();
const add_dialog = ref(false);
const invite_modal = ref(false);
const {copy} = useClipboard();
const invite_url = "http://localhost:8081/api/sch_mgmt/invite-sch/"
const drawer = ref(null);
const sch_info = ref([]);
const send_msg = ref();
const {user_info} = history.state;
const message_list = ref();
const chat_array = ref([]);
// const user_email = history.state.email;


const state = reactive({
  files: [],
});

const { getRootProps, isDragActive, ...rest } = useDropzone({
  onDrop,
});

watch(state, () => {
  console.log('state', state);
});

watch(isDragActive, () => {
  console.log('isDragActive', isDragActive.value, rest);
});

function onDrop(acceptFiles, rejectReasons) {
  console.log(acceptFiles);
  console.log(rejectReasons);
  state.files = acceptFiles;
}

function handleClickDeleteFile(index) {
  state.files.splice(index, 1);
}

const sock = new SockJS("http://localhost:8081/stomp/chat");
const client = webstomp.over(sock); // sockJS를 내부에 들고 있는 client를 내어준다.

let subscription = null; // 구독을 추적하기 위한 변수
client.connect({}, () => {

  watch(drawer, () => {
    if (drawer.value === true) {
      getMessage(sch_info.value.seq);
      if (!subscription) {
        subscription = client.subscribe('/sub/chat/room/' + sch_info.value.seq, (chat) => {
          chat_array.value.push(JSON.parse(chat.body))
        });
      }
    } else if (drawer.value === false && sch_info.value.seq != null) {
      if (subscription) {
        subscription.unsubscribe(); // 이전 구독 해지
        subscription = null;
        chat_array.value = []; // 채팅 배열 초기화
      }
    }
  });
})

const getBase64 = file => {
  return new Promise(resolve => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result;
      resolve(result);
    }
    reader.readAsDataURL(file);
  })
}
const chat = async () => {
  if(state.files !== null && state.files[0] instanceof Blob) {
    send_msg.value = await getBase64(state.files[0]);
    client.send('/pub/chat/message', JSON.stringify({ room_id: sch_info.value.seq, email: user_info.email, blob_type: send_msg.value, send_time: now() }));
    state.files = null;
    send_msg.value = "";
  }
  if(send_msg.value !== undefined) {
    alert(`${send_msg.value}`)
    client.send('/pub/chat/message', JSON.stringify({ room_id: sch_info.value.seq, email: user_info.email, content: send_msg.value, send_time: now() }));
    send_msg.value = "";
  }
}

// 로그인한 유저가 생성한 일정
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

function getMessage(roomId) {
  axios.get('api/chat/getAll?roomId=' + roomId)
      .then((res) => {
        message_list.value = res.data;
      });
}

// 일정 삭제 (내가 생성한 것만)
function deleteSchedule(s_id) {

  axios.post('api/sch_mgmt/deleteOne', s_id)
      .then((res) => {
        if (res.data.value === 1) {
          alert("삭제 완료");
          router.go(0); // 새로고침
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
  <div class="total_div">
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

        <v-spacer></v-spacer>
        <v-btn @click="invite_modal = false; inviteAgree(false)">
          거절
        </v-btn>

        <v-btn @click="invite_modal = false; inviteAgree(true) ">
          수락
        </v-btn>

    </v-card>
  </v-dialog>

<!--  로그인한 유저의 모든 일정 리스트 -->
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
            <v-icon
                icon="mdi-forum-outline"
                @click="drawer = !drawer; sch_info.seq = schedule.seq"
            ></v-icon>
            <v-icon>mdi-pencil</v-icon>
          </v-hover>
        </v-expansion-panel-title>

        <v-expansion-panel-text>
          <div class="each">
            <v-icon style="margin-right: 10px;">mdi-bullhorn-outline</v-icon>
            <span> {{ schedule.content }} </span>
          </div>
          <div class="each">
            <v-icon>mdi-calendar-range</v-icon>
            <span style="margin-bottom: 10px;"> {{useDateFormat(schedule.duedate, ref('MM-DD HH:mm')) }}</span>
          </div>
          <div class="each">
            <v-icon>mdi-map-marker-outline</v-icon>
            <a :href="'https://map.kakao.com/?q=' + schedule.place"  target="_blank" style="color: black; margin-bottom: 10px;"> {{schedule.place}}</a> <br>
            <v-icon @click="copy(invite_url+schedule.seq)">mdi-account-plus</v-icon>
          </div>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

<!--  초대된 일정 리스트 -->
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
          <div class="justify-center align-center">
            <v-icon
                v-bind="props"
                :class="{'on-hover' : isHovering}"
                icon="mdi-message-text-outline"
                @click.stop="drawer = !drawer"
                v-on:click="sch_info.seq = schedule.seq"
            ></v-icon>
            {{drawer}}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <strong> {{ schedule.content }} </strong> <br>
          <span> 일정 </span>
          <span> {{ useDateFormat(schedule.duedate, 'MM-DD HH:mm') }}</span>
          <span> 장소 </span>
          <span> {{ schedule.place }}</span>
          <span>{{ schedule.seq }}</span>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

  <v-btn
      style="float: right; display:block;"
      icon="mdi-plus"
      fab
      elevation="11"
      @click="add_dialog = true"

  >
  </v-btn>

<!--일정 추가 다이얼로그-->
  <v-dialog
      v-model="add_dialog"
      width="auto"
  >

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

<!-- chat sidebar -->
  <v-layout>
    <v-navigation-drawer
        v-model="drawer"
        temporary
        width="450"
    >
      <v-list-item>{{sch_info.seq}} Chat</v-list-item>
      <v-divider></v-divider>
      <v-list
          class="overflow-auto"
          height="700"
      >
<!--      db 저장 정보-->
        <div
            class="chat"
            v-for="message in message_list"
            :key="message"
        >
          <div
              v-if="message.email !== user_info.email"
          >
            {{message.email}}
          </div>
          <div
              :class="{'msg sent' : message.email === user_info.email, 'msg rcvd' : message.email !== user_info.email}"
          >
            {{message.content}}
            <template v-if="message.blob_type">
              <Image :src="message.blob_type" alt="missing" style="max-height:100px; max-width:100px;" preview/>
<!--              <img :src="message.blob_type" alt="missing" style="max-height:250px; max-width:250px;"/>-->
            </template>
            {{useDateFormat(message.send_time, 'HH:mm')}}
          </div>
        </div>

       <!--        stomp 연결 -->
        <div
            class="chat"
            v-for="(item, index) in chat_array"
            :key="index"
        >
          <div
              v-if="item.email !== user_info.email"
          >
            {{item.email}}
          </div>
          <div
              :class="{'msg sent' : item.email === user_info.email, 'msg rcvd' : item.email !== user_info.email}"
          >
            {{item.content}}
            <template v-if="item.blob_type">
              <Image :src="item.blob_type" alt="Image" width="250" preview />
<!--              <img :src="item.blob_type" alt="missing" style="max-height:250px; max-width: 250px;" class="[data-zoomable]">-->
            </template>

            {{useDateFormat(item.send_time, 'HH:mm')}}
          </div>
        </div>
      </v-list>
      <v-divider></v-divider>
      <v-list>
        <v-form @submit.prevent="chat">
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="send_msg"
                    v-bind="getRootProps()"
                    label="채팅 메시지"
                    type="text file"
                    variant="filled"
                    append-icon="mdi-send"
                    append-inner-icon="mdi-map-maker"
                    @click:append="chat"
                >
                  <div class="file-item" v-for="(file, index) in state.files" :key="index">
                    <span>{{ file.name }}</span>
                    <span class="delete-file" @click="handleClickDeleteFile(index)">Delete</span>
                  </div>
                </v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-form>
      </v-list>

    </v-navigation-drawer>
  </v-layout>
  </div>
</template>
<style scoped>

h2 {
  text-align: center;
  margin-bottom: 30px;
}

.total_div {
  width:50%;
  margin: 0 auto;
  margin-top:30px;
}
.list_div {
  margin-bottom: 100px;
}

.invite_div {
  margin-bottom: 50px;
}

.each {
  padding: 10px;
}

.schedule_list {
  margin: 0 auto;
  background-color: #F5F7FA;
  width: 70%;
  padding: 8px 0px 10px 0px;
  border-radius: 8px;
}

.on-hover {
  background-color: lightgray;
  border-radius: 50%;
}

.trash_can {
  margin-right: 50px;
  padding: 15px;
}

.file-item {
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgb(255 167 18 / 20%);
  padding: 7px;
  padding-left: 15px;
  margin-top: 10px;

  &:first-child {
    margin-top: 0;
  }

  .delete-file {
    background: red;
    color: #fff;
    padding: 5px 10px;
    border-radius: 8px;
    cursor: pointer;
  }
}


* {margin: 0; box-sizing: border-box;}

.chat {
  --rad: 20px;
  --rad-sm: 3px;
  font: 16px/1.5 sans-serif;
  display: flex;
  flex-direction: column;
  padding: 20px;
  max-width: 500px;
  margin: auto;
}

.msg {
  position: relative;
  max-width: 75%;
  padding: 7px 15px;
  margin-bottom: 2px;
}

.msg.sent {
  border-radius: var(--rad) var(--rad-sm) var(--rad-sm) var(--rad);
  background: #42a5f5;
  color: #fff;
  /* moves it to the right */
  margin-left: auto;
}

.msg.rcvd {
  border-radius: var(--rad-sm) var(--rad) var(--rad) var(--rad-sm);
  background: #f1f1f1;
  color: #555;
  /* moves it to the left */
  margin-right: auto;
}

/* Improve radius for messages group */

.msg.sent:first-child,
.msg.rcvd+.msg.sent {
  border-top-right-radius: var(--rad);
}

.msg.rcvd:first-child,
.msg.sent+.msg.rcvd {
  border-top-left-radius: var(--rad);
}


/* time */

.msg::before {
  content: attr(data-time);
  font-size: 0.8rem;
  position: absolute;
  bottom: 100%;
  color: #888;
  white-space: nowrap;
  /* Hidden by default */
  display: none;
}

.msg.sent::before {
  right: 15px;
}

.msg.rcvd::before {
  left: 15px;
}


/* Show time only for first message in group */

.msg:first-child::before,
.msg.sent+.msg.rcvd::before,
.msg.rcvd+.msg.sent::before {
  /* Show only for first message in group */
  display: block;
}

</style>
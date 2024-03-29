<script setup>
import axios from "axios";
import {computed, reactive, ref, watch} from "vue";
import {now, useDateFormat} from '@vueuse/core';
import {useField, useForm} from "vee-validate";
import router from "@/router";
import webstomp from "webstomp-client";
import SockJS from "sockjs-client";
import {useDropzone} from "vue3-dropzone";

const schedule_list = ref();
const invited_user_list = ref([]);
const invite_list = ref();
const add_dialog = ref(false);
const update_dialog = ref(false);
const invite_modal = ref(false);
const invite_url = "http://192.168.0.123:8081/api/sch-mgmt/invite-sch/";
const drawer = ref(null);
const sch_info = ref([]);
const update_seq = ref([]);
const update_title = ref([]);
const update_content = ref([]);
const update_duedate = ref([]);
const update_place = ref([]);
const send_msg = ref();
const {user_info} = history.state;
const message_list = ref();
const chat_array = ref([]);
const isDataLoaded = ref(false);
const state = reactive({
  files: [],
});

function copy(schId) {
  navigator.clipboard.writeText(invite_url+schId);

  alert("링크가 복사되었습니다.");
}

const {getRootProps, isDragActive, ...rest} = useDropzone({
  onDrop,
});

fetchData();

async function fetchData() {
  try {
    const res1 = await axios.post('api/sch-mgmt/main-board', user_info)
    schedule_list.value = res1.data
    const res2 = await axios.get('api/sch-mgmt/invite-board?email=' + user_info.email);
    invite_list.value = res2.data;
    const res3 = await axios.get('api/sch-mgmt/check-invite?email=' + user_info.email);

    if (res3.data.toString() === "true") {
      invite_modal.value = true;
    }

    isDataLoaded.value = true;
  } catch (e) {
    // 로그인을 하지 않고 main url로 접근 했을 때 에러 코드
    if (e.response.data.code === 'ACCOUNT-005' && e.response.status === 400) {
      await alert(e.response.data.message);
      await router.push({name: 'login'});
    } else {
      // access token이 휴효하지 않을 때
      alert(e.response.data.message);
    }
  }
}

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

const sock = new SockJS("http://192.168.0.123:8081/stomp/chat");
const client = webstomp.over(sock); // sockJS를 내부에 들고 있는 client를 내어준다.

let subscription = null; // 구독을 추적하기 위한 변수

client.connect({}, () => {

  watch(drawer, () => {
    if (drawer.value === true) {
      getMessage(sch_info.value.seq);
      getInvtedUser(sch_info.value.seq);
      if (!subscription) {
        subscription = client.subscribe('/sub/chat/room/' + sch_info.value.seq, (chat) => {
          chat_array.value.push(JSON.parse(chat.body))
          console.log(chat_array.value)
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
  const chatDiv = document.querySelector(".chat_info");

  if (state.files != null && state.files[0] instanceof Blob) {
    send_msg.value = await getBase64(state.files[0]);
    client.send('/pub/chat/message', JSON.stringify({
      room_id: sch_info.value.seq,
      nickname: user_info.nickname,
      blob_type: send_msg.value,
      send_time: now()
    }));
    state.files = null;
    send_msg.value = null;
    send_msg.value = null;
  }

  if (send_msg.value != null) {
    client.send('/pub/chat/message', JSON.stringify({
      room_id: sch_info.value.seq,
      nickname: user_info.nickname,
      content: send_msg.value,
      send_time: now()
    }));
    send_msg.value = null;
  }

  chatDiv.scrollTop = 20 * chatDiv.scrollHeight;
}

// 채팅 db에 저장된 기록 가져오기
function getMessage(roomId) {
  axios.get('api/chat/getAll?roomId=' + roomId)
      .then((res) => {
        message_list.value = res.data;
        console.log(message_list.value)
      });
}

// 채팅방에 초대된 유저 목록 가져오기
function getInvtedUser(roomId) {
  axios.get('api/users-mgmt/invited-user?roomId=' + roomId)
      .then((res) => {
        invited_user_list.value = res.data;
      });
}

// 해당 채팅방 유저 명 수
const getUserCount = computed(() => invited_user_list.value.length)

// 일정 삭제 (내가 생성한 것만)
function deleteSchedule(s_id) {
  axios.post('api/sch-mgmt/deleteOne', s_id)
      .then((res) => {
        if (res.data.value === 1) {
          alert("삭제 완료");
        }
        router.go(0); // 새로고침
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
      if (value?.length <= 30) return true

      return '30자 이하로 입력해주세요,';
    },
  }
})

const title = useField('title');
const content = useField('content');
const place = useField('place');
const duedate = useField('duedate');

const scheduleSave = handleSubmit(values => {
  values.email = user_info.email;
  axios.post('api/sch-mgmt/save', values)
      .then(res => {
        if (res.data) {
          router.go(0);
        } else {
          alert('error 발생');
        }
      })
      .catch(err => console.log(err))
})

const scheduleUpdate = handleSubmit(values => {
  values.email = user_info.email;
  values.seq = update_seq.value.seq;
  axios.post('api/sch-mgmt/update', values)
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
  <div v-if="isDataLoaded">

    <div class="total_div">
      <h2>{{ user_info.nickname }}님의 Moim</h2>
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
            </v-expansion-panel-title>

            <v-expansion-panel-text>
              <div class="each">
                <v-icon style="margin-right: 10px;">mdi-bullhorn-outline</v-icon>
                <span> {{ schedule.content }} </span>
              </div>
              <div class="each">
                <v-icon>mdi-calendar-range</v-icon>
                <span style="margin-bottom: 10px;">{{ useDateFormat(schedule.duedate, ref('MM-DD HH:mm')) }}</span>
              </div>
              <div class="each">
                <v-icon>mdi-map-marker-outline</v-icon>
                <a :href="'https://map.kakao.com/?q=' + schedule.place" target="_blank"
                   style="color: black; margin-bottom: 10px;"> {{ schedule.place }}</a> <br>
              </div>
              <div class="each">
              </div>
              <div class="icon_div">
                <v-icon
                    icon="mdi-trash-can"
                    @click="deleteSchedule(schedule.seq)"
                ></v-icon>
                <v-icon
                    icon="mdi-forum-outline"
                    @click="drawer = !drawer; sch_info.seq = schedule.seq; sch_info.title = schedule.title"
                ></v-icon>
                <v-icon
                    icon="mdi-pencil"
                    @click="update_dialog = true;
                update_seq.seq = schedule.seq;
                update_title.title = schedule.title;
                update_content.content = schedule.content;
                update_duedate.duedate = schedule.duedate;
                update_place.place = schedule.place;"
                ></v-icon>
                <v-icon
                    icon="mdi-share"
                    @click="copy(schedule.seq)"
                >
                </v-icon>

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

            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <div class="each">
                <v-icon style="margin-right: 10px;">mdi-bullhorn-outline</v-icon>
                <span> {{ schedule.content }} </span>
              </div>
              <div class="each">
                <v-icon>mdi-calendar-range</v-icon>
                <span style="margin-bottom: 10px;">{{ useDateFormat(schedule.duedate, ref('MM-DD HH:mm')) }}</span>
              </div>
              <div class="each">
                <v-icon>mdi-map-marker-outline</v-icon>
                <a :href="'https://map.kakao.com/?q=' + schedule.place" target="_blank"
                   style="color: black; margin-bottom: 10px;"> {{ schedule.place }}</a> <br>
              </div>
              <div class="icon_div">
                <v-icon
                    icon="mdi-forum-outline"
                    @click="drawer = !drawer; sch_info.seq = schedule.seq; sch_info.title = schedule.title "
                ></v-icon>
              </div>
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

      <!--  일정 수정 다이얼로그  -->
      <v-dialog
          v-model="update_dialog"
          width="auto"
      >
        <v-card
            width="800"
            prepend-icon="mdi-plus"
            title="일정 수정하기"
        >
          <v-card-text>
            <form @submit.prevent="scheduleUpdate">
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
                <input
                    type="datetime-local"
                    v-model="duedate.value.value"
                >
              </v-row>
              <v-divider></v-divider>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                    text="닫기"
                    variant="plain"
                    @click="update_dialog = false"
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
          <div style="height: 60px; text-align: center; margin-top: 10px;"><strong>{{ sch_info.title }} 채팅방</strong></div>
          <v-icon>mdi-account-multiple</v-icon>
          <span> {{ getUserCount }} 명</span>
<!--          <div-->
<!--              v-for="users in invited_user_list"-->
<!--              :key="users"-->
<!--          >-->
<!--            <span v-if="user_info.nickname !== users.nickname">{{users.nickname}}</span>-->
<!--          </div>-->
          <v-divider></v-divider>
            <div class="chat_info">
              <!--      db 저장 정보-->
              <div
                  class="chat"
                  v-for="message in message_list"
                  :key="message"
              >
                <div
                    v-if="message.nickname !== user_info.nickname"
                >
                  {{ message.nickname }} <br>
                </div>
                <div
                    :class="{'msg sent' : message.nickname === user_info.nickname, 'msg rcvd' : message.nickname !== user_info.nickname}"
                >
                  {{ message.content }}
                  {{ useDateFormat(message.send_time, 'HH:mm') }}
                  <template v-if="message.blob_type">
                    <img :src="message.blob_type" alt="missing" style="max-height:250px; max-width:250px;"/>
                  </template>
                </div>
              </div>

              <!--        stomp 연결 -->
              <div
                  class="chat"
                  v-for="(item, index) in chat_array"
                  :key="index"
              >
                <div
                    v-if="item.nickname !== user_info.nickname"
                >
                  {{ item.nickname }} <br>
                </div>
                <div
                    :class="{'msg sent' : item.nickname === user_info.nickname, 'msg rcvd' : item.nickname !== user_info.nickname}"
                >
                  <div v-if="item.content !== null">
                    {{ item.content }}
                    {{ useDateFormat(item.send_time, `HH:mm`) }}
                  </div>
                  <template v-if="item.blob_type">
                    <img :src="item.blob_type" alt="missing" style="max-height:250px; max-width: 250px;">
                    {{ useDateFormat(item.send_time, `HH:mm`) }}
                  </template>
                </div>
              </div>
            </div>

          <v-divider></v-divider>

          <div class="chat_field">
            <v-form @submit.prevent="chat">
                <v-row>
                  <v-col>
                    <v-text-field
                        v-model="send_msg"
                        v-bind="getRootProps()"
                        label="채팅 메시지"
                        type="text file"
                        variant="filled"
                        append-icon="mdi-send"
                        append-inner-icon="mdi-map-maker"
                        @click:append="chat();"
                    >
                      <div class="file-item" v-for="(file, index) in state.files" :key="index">
                        <span>{{ file.name }}</span>
                        <span class="delete-file" @click="handleClickDeleteFile(index)">Delete</span>
                      </div>
                    </v-text-field>
                  </v-col>
                </v-row>
            </v-form>
          </div>
        </v-navigation-drawer>
      </v-layout>
    </div>
  </div>

</template>
<style scoped>

.chat_info {
  min-height: 800px;
  max-height: 830px;
  overflow-y: auto;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
}

.total_div {
  width: 50%;
  margin: 0 auto;
  margin-top: 30px;
}

.list_div {
  margin-bottom: 100px;
  padding: 20px;
  //background-color: #191919;
  border-radius: 20px;
}

.invite_div {
  margin-bottom: 50px;
  //background-color: lightgray;
  padding: 20px;
  border-radius: 20px;
}

.each {
  padding: 10px;
}

v-expansion-panels {
  background-color: #F5F7FA;
}

.schedule_list {
  margin: 0 auto;
  background-color: #F5F7FA;
  width: 70%;
  padding: 8px 0px 10px 0px;
  border-radius: 8px;
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


* {
  margin: 0;
  box-sizing: border-box;
}

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
.msg.rcvd + .msg.sent {
  border-top-right-radius: var(--rad);
}

.msg.rcvd:first-child,
.msg.sent + .msg.rcvd {
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
.msg.sent + .msg.rcvd::before,
.msg.rcvd + .msg.sent::before {
  /* Show only for first message in group */
  display: block;
}

.icon_div {
  padding: 10px;
  float: right;
}

.chat_field {
  padding: 8px;
  margin-top: 30px;
}

</style>
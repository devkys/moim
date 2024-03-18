<script setup>
import { onMounted} from "vue";
import axios from "axios";
import {ref} from "vue";
import {useClipboard} from '@vueuse/core';
import { useRoute } from 'vue-router';
const schedule_list = ref();
const add_dialog=ref(false);
const { copy } = useClipboard();
const invite_url="http://localhost:8081/api/sch_mgmt/invite-sch/"

const route = useRoute();

onMounted(() => {
    axios({
    url: 'api/sch_mgmt/main-board='+route.query.email,
    method: 'get',
  }).then((response) => {
    if (response.status === 200) {
      schedule_list.value = response.data;
    }
  }).catch((e) => {
    console.log(`${e.name}(${e.code} : ${e.message})`);
  });
});

</script>

<template>

  <h2></h2>
  <div id="map"></div>
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
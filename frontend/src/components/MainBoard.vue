<script setup>
import { onMounted} from "vue";
import axios from "axios";
import {ref} from "vue";

const schedule_list = ref();
const dialog=ref(false);

onMounted(async () => {
  try {
    const response = await axios.get('api/sch_mgmt/all?user=kks@gmail.com');
    if (response.status === 200) {
      schedule_list.value = response.data;
    }
  } catch (error) {
    console.error(error);
  }


  initMap();

     /* global kakao */
  // if (window.kakao && window.kakao.maps) {
  //   initMap();
  // } else {
  //   const script = document.createElement('script');
  //   script.onload = () => kakao.maps.load(initMap);
  //   script.src = 'http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=6ef9d59fed893f2d0123c41617be5fcd';
  //   document.head.appendChild(script);
  // }
});

function initMap() {
  var container = document.getElementById('map');
  console.log('container:' + container);
  var options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
  };

  var map = new kakao.maps.Map(container, options);
  map.setMapTypeId(kakao.maps.MapTypeId.HYBRID);
}
// onMounted(() => {
//     axios({
//     url: 'api/sch_mgmt/all?user=kks@gmail.com',
//     method: 'get',
//   }).then((response) => {
//     if (response.status === 200) {
//       schedule_list.value = response.data;
//     }
//   }).catch((e) => {
//     console.log(`${e.name}(${e.code} : ${e.message})`);
//   });
//
//   if(window.kakao && window.kakao.maps) {
//     initMap();
//   } else {
//     const script = document.createElement('script');
//     /* global kakao */
//     script.onload = () => kakao.maps.load(initMap());
//     script.src = 'http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=6ef9d59fed893f2d0123c41617be5fcd';
//     document.head.appendChild(script);
//   }
// });
//


  // function initMap() {
  //   var container = document.getElementById('map');
  //   if(!container) return;
  //   var options = {
  //     center: new kakao.maps.LatLng(33.450701, 126.570667),
  //     level: 3
  //   };
  //
  //   var map = new kakao.maps.Map(container, options);
  //   map.setMapTypeId(kakao.maps.MapTypeId.HYBRID);
  // }
</script>

<template>

  <h2>hena의 모임</h2>
  <div id="map"></div>
  <div class="list_div">
    <v-expansion-panels  variant="popout" >
      <v-expansion-panel
          v-for="schedule in schedule_list"
          :key="schedule"
          class="schedule_list"
          expand-icon="expand"
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
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>


  </div>

  <v-col cols="auto">
    <v-btn icon="mdi-plus" fab absolute bottom elevation="11" @click="dialog = true"></v-btn>
  </v-col>
  <v-dialog
      v-model="dialog"
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
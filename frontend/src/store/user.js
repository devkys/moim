import {defineStore} from "pinia";
import {ref} from 'vue';

// 피니아 유저 상태 관리
// 피니아가 이것을 상태로 인식하게 하려면, 셋업 스토어에서 모든 상태 속성을 반환해야 함.
// 다시 말해, 스토어에서는 비공개 상태 속성을 가질 수 없음
// 피니아를 최대한 활용하려면 각 스토어는 다른 파일에 정의해야 한다.
export const useUserStore = defineStore('user', () => {
        const email = ref()
        const nickname = ref()
        const access_token = ref()
        const codeValue = ref()

        return {email, nickname, access_token, codeValue}

    },
    {
        // refresh해도 초기화 x
        persist: true
    })
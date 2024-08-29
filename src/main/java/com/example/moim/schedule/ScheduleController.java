package com.example.moim.schedule;

import com.example.moim.config.security.CustomException;
import com.example.moim.config.security.ErrorCode;
import com.example.moim.member.LoginDTO;
import com.example.moim.member.MemberService;
import com.example.moim.room.Room;
import com.example.moim.room.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("api/sch-mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final MemberService memberService;

    private final RoomService roomService;

    public static final String redirect_url = "http://192.168.0.123:8081/api/sch-mgmt/main-board";

    public static final String client_secret = "BEA593123F504846B43FE11E0E0762720130A845";


    /*
        유저의 일정 리스트 반환
        *. 내가 직접 생성한 일정만 반환
        반환값 List<SchedultDTO>
     */
    @PostMapping("main-board")
    @ResponseBody
    public void getAllPost(@RequestBody Optional<LoginDTO> loginMember) {

        // 로그인 서비스 없이 main api를 호출했을 때 예외 발생 시킴
        loginMember.orElseThrow(()-> new CustomException(ErrorCode.WRONG_ACCESS));

        System.out.println("================DBridge API access token으로 호출=========================\n");
        System.out.println("프론트에서 받은 LoginMember email\t" + loginMember.get().getEmail());
        System.out.println("프론트에서 받은 LoginMember nickname\t" + loginMember.get().getNickname());
        System.out.println("프론트에서 받은 LoginMember Code Value(Auth Code)\t" + loginMember.get().getCodeValue());
        System.out.println("프론트에서 받은 LoginMember access token\t" + loginMember.get().getAccess_token());
        System.out.println("프론트에서 받은 LoginMember access token 만료 기간\t" + loginMember.get().getExpires_in());

        //토근 재발급 uri
        String token_url = "https://dev-api.cyber-i.com/svc/moim/token?code=" + loginMember.get().getCodeValue() + "&client_id=firstClient&client_secret=" + client_secret +
                "&grant_type=authorization_code&redirect_uri=" + redirect_url + "refresh_token=" + memberService.getRefreshTkn(loginMember.get().getEmail());


        // 헤더에 토큰 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", loginMember.get().getAccess_token());

        // 외부 api를 호출할 WebClient 생성
//        WebClient webClient = WebClient.create();
//
//        // response를 String으로 가져오기
//        String mainInfo = webClient.get()
//                .uri("https://dev-api.cyber-i.com/svc/moim/main?email=" + loginMember.get().getEmail())
//                .headers(h -> h.addAll(headers)) // 헤더값 발급받은 access token으로 셋팅
//                .retrieve() // body를 받아서 디코딩
//                .onStatus(HttpStatus.UNAUTHORIZED::equals, // 401 에러 발생 -> access token 유효기간 만료
//                        response -> Mono.error(
//                                new CustomException(ErrorCode.UNAUTHORIZED_KEY)
//                        )).bodyToMono(String.class).block(); // 동기식으로 body 데이터만 받기


        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = mapper.readTree(mainInfo);
//
//        int resSize = rootNode.get("schOut").size();
//        System.out.println("응답 받은 객체 사이즈 " + resSize);
//
//        List<ScheduleDTO> scheduleDTOS = mapper.readValue(rootNode.get("schOut").toString(), new TypeReference<>() {});
//
//        System.out.println("DBridge로 부터 응답 받은 스케쥴 리스트 " + scheduleDTOS);

//        return scheduleDTOS;

    }

    /*
        초대된 일정 반환
     */
    @GetMapping("invite-board")
    @ResponseBody
    public List<Schedule> invitedGet(@RequestParam("email") String email) {
        return scheduleService.getInvitedSchedule(email);
    }


    /*
        초대 링크 클릭시, 세션 생성과 로그인 화면으로 리다이렉션
        * 해당 일정의 시퀀스 번호로 세션 생성
     */
    @GetMapping("invite-sch/{id}")
    public RedirectView inviteSchedule(@PathVariable("id") String id, HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute("sch_id", id);
        System.out.println("--------------------------" + session.getAttribute("sch_id"));
        return new RedirectView("http://192.168.0.123:5173/login");
    }

    /*
        초대된 일정 세션 존재하는지 확인
        존재 시 초대된 일정 pk값 리턴
     */
    @GetMapping("check-invite")
    @ResponseBody
    public String confirmInvite(HttpServletRequest req, @RequestParam("email") String email) throws IllegalAccessException {

        HttpSession session = req.getSession();
        Object sch_id = session.getAttribute("sch_id");

        // 세션 값 null
        if (sch_id == null) {
            System.out.println("초대장 없음");
            return "false";
        } else {
            /*
                세션 값 not null
             */
            Room room = roomService.alreadyEnter(email, (Long.parseLong(sch_id.toString())));

            if (scheduleService.getEmail(Long.parseLong(sch_id.toString())).equals(email) || room != null) {
                // 세션 값은 있지만 이미 초대된 방의 링크를 클릭했을 때 세션 끊기
                session.invalidate();
                return "false";
            }
            System.out.println("초대장 있음 " + sch_id);
            return "true";
        }
    }

    @PostMapping("save")
    @ResponseBody
    public boolean saveSchedule(@RequestBody Schedule reqData) {
        System.out.println("생성하고 싶은 일정 " + reqData);
        scheduleService.save(reqData);
        return true;
    }

    @PostMapping("update")
    @ResponseBody
    public boolean updateSchedule(@RequestBody Schedule schedule) {
        System.out.println("생성하고 싶은 일정 " + schedule);
//        scheduleService.update(reqData.getTitle(), reqData.getContent(), reqData.getDuedate(), reqData.getPlace(), reqData.getSeq());
        scheduleService.update(schedule);
        return true;
    }

    @PostMapping("deleteOne")
    @ResponseBody
    public int deleteSchedule(@RequestBody String scheduleId) {
        System.out.println("스케쥴 삭제" + scheduleId);
        scheduleService.deletebyId(Long.parseLong(scheduleId));

        return 1;
    }

}

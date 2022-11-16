package com.zerobase.fastlms.member.controller;

import com.zerobase.fastlms.common.model.ResponseResult;
import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.model.TakeCourseInput;
import com.zerobase.fastlms.course.service.TakeCourseService;
import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor // 생성자를 추가해줌
@RestController
public class ApiMemberController {
    private final TakeCourseService takeCourseService;

    @PostMapping("/api/member/course/cancel.api")
    public ResponseEntity<?> cancelCourse(Model model /*ajax 를 리턴하는 리턴타입임*/
            , @RequestBody TakeCourseInput parameter /*RequestBody로 받아야 함*/
            , Principal principal) {

        String userId = principal.getName(); // principal : 내가 로그인한 계정임

        // 내 강좌인지 확인 => 내 강좌여야지 취소 권한이 있음
        // api 호출은 조작 가능하기 때문에 데이터 변경 전 서버에서 체크 다시 해줘야 하기 때문에 위를 다시 확인해야함
        TakeCourseDto detail = takeCourseService.detail(parameter.getTakeCourseId());
        if (detail == null) {
            ResponseResult responseResult = new ResponseResult(false, "수강 신청 정보가 존재하지 않습니다.");
            return ResponseEntity.ok().body(responseResult);
        }

        // 강좌를 취소하겠다고 한 사람이 실제 그 강좌를 신청한 user 인지 꼭 확인해야 함
        if (userId == null || !userId.equals(detail.getUserId())) {
            // 내 수강신청 정보가 아닌것임
            ResponseResult responseResult = new ResponseResult(false, "본인의 수강 신청 정보만 취소할 수 있습니다.");
            return ResponseEntity.ok().body(responseResult);
        }

        // 수강 목록이 여러개일 수 있으므로 List로 받음
        ServiceResult result = takeCourseService.cancel(parameter.getCourseId());
        if (!result.isResult()) { // 정상이 아닐 때
            ResponseResult responseResult = new ResponseResult(false, result.getMessage());
            return ResponseEntity.ok().body(responseResult);
        }

        ResponseResult responseResult = new ResponseResult(true);
        return ResponseEntity.ok().body(responseResult);
    }
}

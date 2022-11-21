package com.zerobase.fastlms.member.controller;

import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.components.RequestUtils;
import com.zerobase.fastlms.course.service.TakeCourseService;
import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor // 생성자를 추가해줌
@Controller
public class LoginHistoryController {
    private final MemberService memberService;
    private final TakeCourseService takeCourseService;

    private final MailComponents mailComponents;

    @GetMapping("/member/loginHistory")
    public String index() {

        /*String userAgent = RequestUtils.getUserAgent(request);
        String clientIp = RequestUtils.getClientIp(request);*/


        return "member/loginHistory";
    }
}

    /*@GetMapping("/member/login")
    public String login() {

        return "member/login";
    }*/

   /* @PostMapping("/member/login") // post는 버튼 클릭시 입력받은 데이터를 서버로 전달
    public String loginSubmit(Model model, // model 쓰기 위해 model 주입 받는다
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 MemberInput parameter,
                                 Principal principal) { *//*회원 정보를 가져오기 위해 Principal 의존성 주입*//*


        String userId = principal.getName();

        // 비밀번호가 일치하면 회원 탈퇴 시킴
        boolean result = memberService.login(userId, parameter.getPassword());
        model.addAttribute("result", result);*/

        //*아래 두줄이 있어야만 로그인이 제대로 된다*//*
        //String userId = principal.getName();

        //String userId = parameter.getUserId();
        //String password = parameter.getPassword();
       /* String userId = principal.getName(); //아이디

        Boolean result = memberService.login(userId);
        model.addAttribute("result", result);*/

        /*boolean result = memberService.login(parameter);
        model.addAttribute("result", result);*/
/*

        return "redirect:/";
    }
*/

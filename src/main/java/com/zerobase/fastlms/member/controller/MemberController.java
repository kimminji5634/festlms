package com.zerobase.fastlms.member.controller;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.service.TakeCourseService;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor // 생성자를 추가해줌
@Controller
public class MemberController {
    private final MemberService memberService;
    private final TakeCourseService takeCourseService;


    @GetMapping("/member/login")
    public String login() {

        return "member/login";
    }

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


    /*@PostMapping("/member/login")
    public String loginSubmit(Model model
            , MemberInput parameter) {

       memberService.login(parameter);

        return "member/login_complete";
    }*/

    @GetMapping("/member/find/password")
    public String findPassword() {

        return "member/find_password";
    }

    @PostMapping("/member/find/password")
    public String findPasswordSubmit(Model model, ResetPasswordInput parameter) {

        // 메서드명 먼저 적고 create method로 생성하는게 훨씬 편함
        boolean result = false;
        try {
            result = memberService.sendResetPassword(parameter);
        } catch (Exception e) {

        }
        model.addAttribute("result", result);

        // 메인 페이지로 이동하게 하려면  return "redirect:/"; 이렇게 해야 한다
        // return "index" 이 경우 주소는 같고 view 만 다름, 주소와 뷰를 둘 다 바뀌게 하기 위해서는 아래처럼
        return "member/find_password_result";
    }

    @GetMapping("/member/register") // get은 화면을 내려줌
    public String register() {

        return "member/register";
    }

    // request 객체 : web -> server 으로 데이터를 보냄
    // response 객체 : server -> web 으로 데이터를 보냄
    // 위 객체를 파라미터에 넘겨주면 스프링이 이 값들을 주입한다
    @PostMapping("/member/register") // post는 버튼 클릭시 입력받은 데이터를 서버로 전달
    public String registerSubmit(Model model, // model 쓰기 위해 model 주입 받는다
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 MemberInput parameter) {

        boolean result = memberService.register(parameter);
        model.addAttribute("result", result);

        return "member/register_complete";
    }

    // 파라미터 받는 방법 2가지 있음
    // 1. 기본적인 방법
    // 파라미터는 클라이언트에서 서버로 전송되는 정보 => request 라는 객체로 가져올 수 있음
    @GetMapping("/member/email-auth")
    // model 해주는 이유 : 뷰(.html, .jsp)에 model 값을 던지기 위해서
    public String emailAuth(Model model, HttpServletRequest request) {

        // 'http://localhost:8080/member/email-auth?id=" + uuid + "'
        String uuid = request.getParameter("id"); // id
        // System.out.println(uuid); => 웹 주소에 http://localhost:8080/member/email-auth?id=123456
        // 이렇게 쓰면 콘솔에 123456인 id가 잘 찍히나 확인하기 위해 해줌

        boolean result = memberService.emailAuth(uuid);
        model.addAttribute("result", result);

        return "member/email-auth";
    }

    @GetMapping("/member/info")
    public String memberInfo(Model model, Principal principal) {

        // 내 정보는 security 안에 있는 session에 담겨 있음 => Principal 이라는 security 인터페이스를 사용해야 함
        String userId = principal.getName();
        MemberDto detail = memberService.detail(userId);

        model.addAttribute("detail", detail);

        return "member/info";
    }

    @PostMapping("/member/info")
    public String memberInfoSubmit(Model model
            , MemberInput parameter /*phone정보만 수정가능하게 만드는데 MemberInput에 phone 파라미터 있으므로 사용*/
            , Principal principal) {

        // 내 정보는 security 안에 있는 session에 담겨 있음 => Principal 이라는 security 인터페이스를 사용해야 함
        String userId = principal.getName();
        parameter.setUserId(userId);

        ServiceResult result = memberService.updateMember(parameter);
        if (!result.isResult()) { // false라면
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }

        return "redirect:/member/info";
    }

    @GetMapping("/member/password")
    public String memberPassword(Model model, Principal principal) {

        // 내 정보는 security 안에 있는 session에 담겨 있음 => Principal 이라는 security 인터페이스를 사용해야 함
        String userId = principal.getName();
        MemberDto detail = memberService.detail(userId);

        model.addAttribute("detail", detail);

        return "member/password";
    }

    @PostMapping("/member/password")
    public String memberPasswordSubmit(Model model
            , MemberInput parameter
            , Principal principal) {

        // 내 정보는 security 안에 있는 session에 담겨 있음 => Principal 이라는 security 인터페이스를 사용해야 함
        String userId = principal.getName();
        parameter.setUserId(userId);

        ServiceResult result = memberService.updateMemberPassword(parameter);
        if (!result.isResult()) { // serviceResult 에 에러가 생기면
            model.addAttribute("message",result.getMessage());
            return "common/error";
        }

        return "redirect:/member/info";
    }

    @GetMapping("/member/takecourse")
    public String memberTakeCourse(Model model, Principal principal) {

        // 내 정보는 security 안에 있는 session에 담겨 있음 => Principal 이라는 security 인터페이스를 사용해야 함
        String userId = principal.getName();

        // 수강 목록이 여러개일 수 있으므로 List로 받음
        List<TakeCourseDto> list = takeCourseService.myCourse(userId);

        model.addAttribute("list", list);

        return "member/takecourse";
    }

    @GetMapping("/member/reset/password")
    // 페이지를 내려줄 때 url 에 있는 id 값을 가져오도록 HttpServletRequest 해줌
    // model 을 쓰면 html 에서 값을 화면에 출력하거나 html 에서 값에 따라 다른 화면에 출력해줄 수 있음
    public String resetPassword(HttpServletRequest request, Model model) {

        String uuid = request.getParameter("id");
        //model.addAttribute("uuid", uuid); // 페이지 내려줄 때 같이 내려줌

        boolean result = memberService.checkResetPassword(uuid);

        model.addAttribute("result", result); // result 값을 model에 담아줌

        return "member/reset_password";
    }

    //** ResetPasswordInput 을 사용해도 웹 주소값 가져올 수 있음
    @PostMapping("/member/reset/password")
    public String resetPasswordSubmit(Model model, ResetPasswordInput parameter) {

        boolean result = false;
        try {
            result =
                    memberService.resetPassword(parameter.getId(), parameter.getPassword());
        }catch (Exception e) {

        }

        // submit 버튼을 눌러 입력값을 서버로 보낼 때 내려줌
        model.addAttribute("result", result); // result 값을 model에 담아줌

        // model => 아래 html에서 사용
        return "member/reset_password_result";
    }

    @GetMapping("/member/withdraw")
    public String memberWithdraw(Model model) {

        return "member/withdraw";
    }

    @PostMapping("/member/withdraw")
    public String memberWithdrawSubmit(Model model
            , MemberInput parameter /*MemberInput을 통해 패스워드 받음*/
                                       /*받을 수 있는 조건! withdraw.html 에서 name=password 로 받음*/
            , Principal principal) { /*회원 정보를 가져오기 위해 Principal 의존성 주입*/

        // 로그인한 세션으로부터 회원정보 가져오기
        String userId = principal.getName();

        // 비밀번호가 일치하면 회원 탈퇴 시킴
        ServiceResult result = memberService.withdraw(userId, parameter.getPassword());
        if (!result.isResult()) { //result가 정상적이 아니면
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }

        // 로그아웃 시켜줘야 함
        return "redirect:/member/logout";
    }
}

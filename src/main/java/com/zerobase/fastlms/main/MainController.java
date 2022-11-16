package com.zerobase.fastlms.main;

import com.zerobase.fastlms.components.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// response 객체를 통해서 한글을 출력시켜보자!!!

// @RestController 는 문자열을 바로 리턴할 수 있지만 @Controller 는 template(뷰) 을 리턴시킨다
// 문자열을 리턴시키고 싶으면 response 객체를 이용해야 한다
@RequiredArgsConstructor
@Controller
public class MainController {
    private final MailComponents mailComponents;

    @RequestMapping ("/") // 요청에 대한 매핑을 해주겠다
    public String index() {

        /* 사용하지 않을 것이므로 주석 처리!
        String email = "kimminji5634@gmail.com";
        String subject = "안녕하세요. 제로베이스 입니다.";
        String text = "<p>안녕하세요.</p><p>반갑습니다.</p>";

        mailComponents.sendMail(email, subject, text);*/

        return "index"; // index.html 파일을 리턴함
    }

    @RequestMapping ("/error/denied")
    public String errorDenied() {

        return "error/denied";
    }
}

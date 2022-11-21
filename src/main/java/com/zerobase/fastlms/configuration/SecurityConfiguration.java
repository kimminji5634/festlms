package com.zerobase.fastlms.configuration;


import com.zerobase.fastlms.components.RequestUtils;
import com.zerobase.fastlms.member.entity.LoginHistory;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.repository.LoginHistoryRepository;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@EnableWebSecurity
@Configuration // config 에 관련된 파일임
// 어노테이션을 붙인다고 기능이 실행되는게 아니라 상속을 받아야 함
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // 상속을 받고 원하는 부분을 오버라이드로 구현 *ctrl + o 누르면 오버라이드 할 수 있는 메서드 나옴
    // WebSecurityConfigurerAdapter_configure(http:HttpSecurity, web:WebSecurity) 두 개 오버라이드

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final LoginHistoryRepository loginHistoryRepository;


    public SecurityConfiguration(MemberRepository memberRepository, MemberService memberService, LoginHistoryRepository loginHistoryRepository, LoginHistoryRepository loginHistoryRepository1) {
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.loginHistoryRepository = loginHistoryRepository1;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/favicon.ico", "/files/**");
        super.configure(web);
    }



    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // UserAuthenticationFailureHandler 가져오기
    UserAuthenticationFailureHandler getFailureHandler() {
        return new UserAuthenticationFailureHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 에러가 떠서 보안상의 문제가 있긴 하지만 아래와 같이 임시 처방 => 에러 메시지 잘 나옴
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin(); /*editor 적용됨*/

        // 페이지 권한 설정
        http.authorizeRequests()
                .antMatchers("/", // index 페이지
                                        "/member/register", // 회원가입
                                        "/member/login_complete",
                // email-auth를 email_auth로 쳐서 계속 에러떴었음... 하
                                        "/member/email-auth",
                                        "/member/find/password",
                                        "/member/reset/password") // 이메일 인증
                // logout 페이지도 제공하는데 로그아웃 후 어느 주소로도 다 들어가는 것 확인 가능
                .permitAll(); // 접근 가능하게 하겠다
        //** 다른 페이지들은 로그인해야 접근이 가능함!!

        http.authorizeRequests()
                        .antMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN");

        http.formLogin()
                .loginPage("/member/login") /*컨트롤러 주소*/
                .loginPage("/member/loginHistory") /*컨트롤러 주소*/
                /*아래는 로그인 성공시 가는 주소인데 기본값 false지만 ,true로 해야 우선순위 1등으로 무조건 아래로 감*/
                .defaultSuccessUrl("/")
                .usernameParameter("username") // 아이디 파라미터명
                .passwordParameter("password") // 패스워드 파라미터명
                .loginProcessingUrl("/member/login") // 로그인 Form Action Url
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                                        HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {
                        System.out.println("authentication:: "+ authentication.getName());
                        response.sendRedirect("/");

                        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        //UserDetails userDetails = (UserDetails)principal;
                        String username = ((UserDetails) principal).getUsername();
                        /*String password = ((UserDetails) principal).getPassword();*/

                        Optional<Member> optionalMember = memberRepository.findById(username);
                        Member member = optionalMember.get();
                        member.setLastLoginDt(LocalDateTime.now());
                        memberRepository.save(member);

                          /*String userAgent = RequestUtils.getUserAgent(request);
                            String clientIp = RequestUtils.getClientIp(request);*/
                        /*아래 get 그냥 안들어가네... 왜지??=> @gmail.com 안넣었음..*/
                        Optional<LoginHistory> optionalLoginHistory = loginHistoryRepository.findById(username);
                        LoginHistory loginHistory = optionalLoginHistory.get();
                        loginHistory.setLoginDt(LocalDateTime.now());
                        String clientIp = RequestUtils.getClientIp(httpServletRequest);
                        loginHistory.setIp(clientIp);
                        String userAgent = RequestUtils.getUserAgent(httpServletRequest);
                        loginHistory.setUserAgent(userAgent);
                        loginHistoryRepository.save(loginHistory);
                    }
                })

                //.successForwardUrl("/member/login")
                .failureHandler(getFailureHandler()) // 로그인 실패시 처리할 수 있음
                .permitAll();


        // member/logout 주소에서 로그아웃 성공되면 루트(/)로 이동하고 세션을 초기화한다~!!
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);

        http.exceptionHandling()
                .accessDeniedPage("/error/denied");

        super.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // userDetailService 는 제공되는 메서드임
        // userDetailsService 에 memberService 정보를 등록하려면 MemberService 에서 상속을 받아야 함
        auth.userDetailsService(memberService)
                .passwordEncoder(getPasswordEncoder()); // getPasswordEncoder 생성해야 함
        super.configure(auth);
    }


}


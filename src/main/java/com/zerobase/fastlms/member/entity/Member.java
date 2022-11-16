package com.zerobase.fastlms.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data // getter, setter 자동 생성
@Entity // 테이블을 말함
public class Member implements MemberCode{

    @Id
    private String userId;

    private String userName;
    private String phone;
    private String password;
    private LocalDateTime regDt; // 회원가입 날짜
    private LocalDateTime uptDt; // 회원정보 수정일

    private boolean emailAuthYn; // 이메일 인증이 y인 경우만 회원가입 되도록
    private LocalDateTime emailAuthDt; // 이메일 인증한 날짜 => emailAuthYn 이 true가 될 때의 시간
    private String emailAuthKey; // 이메일 인증시 키를 만들어 이메일로 보내주는데 => 인증번호에 해당

    private String resetPasswordKey;
    private LocalDateTime resetPasswordLimitDt;

    private boolean adminYn;

    private String userStatus;

    private String zipcode; // 우편번호
    private String addr;
    private String addrDetail;
}

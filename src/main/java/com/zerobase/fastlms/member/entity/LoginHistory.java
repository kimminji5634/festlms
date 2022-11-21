package com.zerobase.fastlms.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data // getter, setter 자동 생성
@Entity // 테이블을 말함
public class LoginHistory implements MemberCode{

    @Id
    private String userId;

    private Integer no;

    // 로그인 날짜
    private LocalDateTime loginDt;

    @Lob // 데이터 양이 많음
    private String ip;

    private String userAgent;
}

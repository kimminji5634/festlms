package com.zerobase.fastlms.admin.dto;

import com.zerobase.fastlms.member.entity.LoginHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data /*getter, setter*/
public class LoginHistoryDto {

    String userId;
    Integer no;
    LocalDateTime loginDt;
    String ip;
    String userAgent;

    /*long seq;
    long totalCount;*/


    public static LoginHistoryDto of(LoginHistory loginHistory) { /*MemberDetail 주입*/

        return LoginHistoryDto.builder()
                .userId(loginHistory.getUserId())
                .no(loginHistory.getNo())
                .loginDt(loginHistory.getLoginDt())
                .ip(loginHistory.getIp())
                .userAgent(loginHistory.getUserAgent())
                .build();
    }


    public String getLoginDtText() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return loginDt != null ? loginDt.format(formatter) : "";
    }
}

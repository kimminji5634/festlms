package com.zerobase.fastlms.member.entity;

import javax.persistence.Id;
import java.time.LocalDateTime;

public interface MemberCode {

    // 현재 가입 요청중 => 이메일 인증 받아야함
    String MEMBER_STATUS_REQ = "REQ";

    // 현재 이용중인 상태
    String MEMBER_STATUS_ING = "ING";

    // 현재 정지된 상태
    String MEMBER_STATUS_STOP = "STOP";
}

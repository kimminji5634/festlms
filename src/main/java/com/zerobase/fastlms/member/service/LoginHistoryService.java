package com.zerobase.fastlms.member.service;


import com.zerobase.fastlms.admin.dto.LoginHistoryDto;
import com.zerobase.fastlms.admin.model.MemberParam;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface LoginHistoryService  { // service는 하나의 트랜잭션을 처리하는 단위


    // 회원 목록 띄우기 위해 List<Member> 가져옴(관리자에서만 사용 가능)
    List<LoginHistoryDto> list(MemberParam parameter);

    // 회원 상세 정보
    LoginHistoryDto loginHistoryDetail(String userId);

    //List<LoginHistory> list(MemberParam parameter);


    // List<LoginHistory> list(MemberParam parameter);
}

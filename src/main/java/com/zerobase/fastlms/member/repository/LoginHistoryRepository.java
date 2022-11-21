package com.zerobase.fastlms.member.repository;

import com.zerobase.fastlms.member.entity.LoginHistory;
import com.zerobase.fastlms.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, String> {

    // 구조만 만들어주면 JPA 가 자동으로 구현해줌
    // Member 테이블에서 emailAuthKey 값을 주면 해당 되는 값 있나 찾아줌
    //Optional<Member> findByEmailAuthKey(String emailAuthKey);
    //Optional<Member> findByUserIdAndUserName(String userId, String userName);
    //Optional<Member> findByResetPasswordKey(String resetPasswordKey);

    /*// 실습 - 로그인시 시간 등록
    Optional<Member> findByUserIdAndPassword(String userId, String password);*/
}

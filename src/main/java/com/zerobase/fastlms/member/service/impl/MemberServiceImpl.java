package com.zerobase.fastlms.member.service.impl;

import java.util.*;

import com.zerobase.fastlms.admin.dto.LoginHistoryDto;
import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.mapper.MemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.entity.MemberCode;
import com.zerobase.fastlms.member.exception.MemberNotEmailAuthException;
import com.zerobase.fastlms.member.exception.MemberStopUserException;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor // 생성자 자동으로 만들어서 주입
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MailComponents mailComponents; // 메일 보내기 위해 필요함
    private final MemberMapper memberMapper;

    @Override
    public boolean login(String userId, String password) {

        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        if (!PasswordUtils.equals(password, member.getPassword())) {
            return false;
        }

        member.setLastLoginDt(LocalDateTime.now());
        memberRepository.save(member);


        /*Optional<Member> optionalMember = memberRepository.findById(userId);

        Member member = optionalMember.get();

        member.setLastLoginDt(LocalDateTime.now());
        memberRepository.save(member);*/

        return true;
    }

    /*public boolean login(MemberInput parameter) {
        *//*login.html에서 아이디가 name=username으로 되어 있음*//*
        Optional<Member> regMember = memberRepository.findById(parameter.getUserId());

        Member member = regMember.get();
        member.setLastLoginDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }*/


    /*
    * 회원가입시 아이디가 중복되면 동일한 아이디가 있다고 알려주기
    * */
    @Override
    public boolean register(MemberInput parameter) { /*회원가입*/

        // findById 들어가보면 타입이 Optional임
        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if (optionalMember.isPresent()) {
            // userId에 해당하는 데이터가 존재
            return false;
        }

        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
        String uuid = UUID.randomUUID().toString();

        // entity 인 Member 클래스에 @Builder 추가함으로 Member 생성시 new 가 아닌 builder를 사용할 수 있다
        Member member = Member.builder()
                .userId(parameter.getUserId())
                .userName(parameter.getUserName())
                .phone(parameter.getPhone())
                .password(encPassword)
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(uuid)
                .userStatus(Member.MEMBER_STATUS_REQ)
                .build();

        memberRepository.save(member); // 테이블에 아래의 값들이 저장됨

        // a target='_blank' : 새 창에서 열리게 해주세요
        String email = parameter.getUserId();
        String subject = "fastlms 사이트에 가입을 축하드립니다.";
        String text = "<p>fastlms 사이트에 가입을 축하드립니다.</p>"
                    + "<p>아래 링크를 클릭하셔서 가입을 완료하세요.</p>"
                    + "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'>가입 완료</a></div>";
        mailComponents.sendMail(email, subject, text);

        return true; // 여기 return 값이 true로 나와야 ${result} 값이 true임!!!=> 회원가입 성공 뜸!!
    }
    @Override
    public boolean emailAuth(String uuid) {

        // findById(id) 가 아니므로 repository 에서 메소드를 하나 생성해줘야 함
        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid); // 테이블에 있나 찾아줘
        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        // EmailAuthYn 가 true = 활성화된 계정이라면
        if (member.isEmailAuthYn()) {
            return false; // 추가적으로 활성화 안시킴 => 활성화 실패 메시지 뜸
        }

        member.setUserStatus(MemberCode.MEMBER_STATUS_ING);
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now()); // true 로 변경될 때의 시간을 저장
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {

        Optional<Member> optionalMember =
                memberRepository.findByUserIdAndUserName(
                        parameter.getUserId(), parameter.getUserName());

        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();
        String uuid = UUID.randomUUID().toString();
        member.setResetPasswordKey(uuid);
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1)); // 제한시간 하루
        memberRepository.save(member);

        // 사용자가 이메일 링크를 타고 들어오면
        String email = parameter.getUserId();
        String subject = "[fastlms] 비밀번호 초기화 메일입니다.";
        String text = "<p>fastlms 비밀번호 초기화 메일입니다.</p>"
                + "<p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p>"
                + "<div><a target='_blank' href='http://localhost:8080/"
                + "member/reset/password?id=" + uuid + "'>비밀번호 초기화 링크</a></div>";
        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean resetPassword(String uuid, String password) {

        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        // uuid값이 맞다면 -> 링크 제대로 들어옴
        Member member = optionalMember.get();

        // 초기화 날짜가 유효한지
        // 초기화를 한번도 안했다면 null인 상태이다
        if (member.getResetPasswordLimitDt() == null) { // 제한날짜에 대한 데이터 없는 경우
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) { // 제한 날짜 기준 넘어섬
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        // 암호화된 pw로 설정
        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        // ResetPasswordKey를 날려서 다시 초기화할 수 없도록
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean checkResetPassword(String uuid) {
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            return false;
        }

        // uuid값이 맞다면 -> 링크 제대로 들어옴
        Member member = optionalMember.get();

        // 초기화 날짜가 유효한지
        // 초기화를 한번도 안했다면 null인 상태이다
        if (member.getResetPasswordLimitDt() == null) { // 제한날짜에 대한 데이터 없는 경우
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) { // 제한 날짜 기준 넘어섬
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        return true;
    }

    @Override
    public List<MemberDto> list(MemberParam parameter) {

        long totalCount = memberMapper.selectListCount(parameter);
        List<MemberDto> list = memberMapper.selectList(parameter);
        // list가 empty 아니라면
        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for (MemberDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
             }
        }

        return list;
        // return memberRepository.findAll(); // 전체 목록 그대로 리턴
    }

    @Override
    public MemberDto detail(String userId) {

        // 쿼리 복잡하게 되어 있으면 mybatis로 작업하고 그 외는 아래와 같이 jpa로 작성
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return null;
        }

        Member member = optionalMember.get();

        return MemberDto.of(member);
    }

    @Override
    public boolean updateStatus(String userId, String userStatus) {

        /*회원정보 가져오기*/
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        member.setUserStatus(userStatus);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean updatePassword(String userId, String password) {
        /*회원정보 가져오기*/
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        member.setPassword(encPassword);
        memberRepository.save(member);

        return true;
    }

    @Override
    public ServiceResult updateMemberPassword(MemberInput parameter) {

        String userId = parameter.getUserId();

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (!PasswordUtils.equals(parameter.getPassword(), member.getPassword())){
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        String encPassword = PasswordUtils.encPassword(parameter.getNewPassword());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return new ServiceResult(true);
    }

    @Override
    public ServiceResult updateMember(MemberInput parameter) {

        String userId = parameter.getUserId();

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        member.setPhone(parameter.getPhone());
        member.setZipcode(parameter.getZipcode());
        member.setAddr(parameter.getAddr());
        member.setAddrDetail(parameter.getAddrDetail());
        member.setUptDt(LocalDateTime.now());
        memberRepository.save(member);

        return new ServiceResult(true);
    }

    @Override
    public ServiceResult withdraw(String userId, String password) {

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (!PasswordUtils.equals(password, member.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        member.setUserName("삭제회원");
        member.setPhone("");
        member.setPassword("");
        member.setRegDt(null);
        member.setUptDt(null);
        member.setEmailAuthYn(false);
        member.setEmailAuthDt(null);
        member.setEmailAuthKey("");
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        member.setUserStatus(MemberCode.MEMBER_STATUS_WITHDRAW);
        member.setZipcode("");
        member.setAddr("");
        member.setAddrDetail("");
        memberRepository.save(member);

        return new ServiceResult();
    }

    // username : email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (Member.MEMBER_STATUS_REQ.equals(member.getUserStatus())) { // false 인 경우
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해주세요.");
        }

        if (Member.MEMBER_STATUS_STOP.equals(member.getUserStatus())) { // false 인 경우
            throw new MemberStopUserException("정지된 회원입니다.");
        }

        if (Member.MEMBER_STATUS_WITHDRAW.equals(member.getUserStatus())) { // false 인 경우
            throw new MemberStopUserException("탈퇴된 회원입니다.");
        }

         List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (member.isAdminYn()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // User 사용하려면 username, password, row 던져야함, User 들어가면 알 수 있음
        return new User(member.getUserId(), member.getPassword(),grantedAuthorities);
    }
}

package com.zerobase.fastlms.member.service.impl;

import java.util.List;
import com.zerobase.fastlms.admin.dto.LoginHistoryDto;
import com.zerobase.fastlms.admin.mapper.HistoryMemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.member.entity.LoginHistory;
import com.zerobase.fastlms.member.repository.LoginHistoryRepository;
import com.zerobase.fastlms.member.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor // 생성자 자동으로 만들어서 주입
@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;
    private final HistoryMemberMapper historyMemberMapper;


    @Override
    public LoginHistoryDto loginHistoryDetail(String userId) {
        Optional<LoginHistory> optionalLoginHistory = loginHistoryRepository.findById(userId);
        if (!optionalLoginHistory.isPresent()) {
            return null;
        }

        LoginHistory loginHistory = optionalLoginHistory.get();

        return LoginHistoryDto.of(loginHistory);
    }

    @Override
    public List<LoginHistoryDto> list(MemberParam parameter) {

        List<LoginHistoryDto> list = historyMemberMapper.selectList(parameter);

        return list;
    }
}

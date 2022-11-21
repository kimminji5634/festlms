package com.zerobase.fastlms.admin.mapper;

import com.zerobase.fastlms.admin.dto.LoginHistoryDto;
import com.zerobase.fastlms.admin.model.MemberParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryMemberMapper {
    long selectListCount(MemberParam parameter);
    List<LoginHistoryDto> selectList(MemberParam parameter);
}

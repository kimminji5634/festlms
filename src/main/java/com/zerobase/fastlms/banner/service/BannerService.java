package com.zerobase.fastlms.banner.service;

import com.zerobase.fastlms.banner.dto.BannerDto;
import com.zerobase.fastlms.banner.model.BannerInput;
import com.zerobase.fastlms.banner.model.BannerParam;

import java.util.List;

public interface BannerService {

    // 강좌 등록
    boolean add(BannerInput parameter);

    // 강좌 정보 수정
    //boolean set(BannerInput parameter);

    // 강좌 목록
    List<BannerDto> list(BannerParam parameter);

    // 강좌 상세정보
    //BannerDto getById(long no);

    // 강좌 내용 삭제
    boolean del(String noList);

    // 프론트 강좌 목록
    //List<BannerDto> frontList(BannerParam parameter);

    // 프론트(사용자가 보는 페이지)에서 강좌 상세 정보
    //BannerDto frontDetail(long no);

    // 수강신청
    //ServiceResult req(TakeCourseInput parameter);

    // 전체 강좌 목록
    //List<BannerDto> listAll();
}

package com.zerobase.fastlms.banner.model;

import lombok.Data;

@Data //getter,setter 쓰도록
public class BannerInput {

    long no;
    String bannerName;
    //String image;

    // 삭제를 위한
    String idList;

    // **ADD 파일
    String filename;
    String urlFilename;

    //링크 등록
    String open;
    int order;
    String yesNo;
}

package com.zerobase.fastlms.banner.dto;

import com.zerobase.fastlms.banner.entity.Banner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BannerDto {

    Long no;
    String bannerName;
    LocalDateTime regDt;

    String filename;
    String urlFilename;

    long totalCount;
    long seq;

    //링크 등록
    String open;
    int order;
    String yesNo;

    /*of 메서드의 의미 : Course entity 를 받아서 CourseDto 로 리턴해줌*/
    public static BannerDto of(Banner banner) {
        return BannerDto.builder()
                .no(banner.getNo())
                .bannerName(banner.getBannerName())
                .regDt(banner.getRegDt())
                .filename(banner.getFilename())
                .urlFilename(banner.getUrlFilename())
                .open(banner.getOpen())
                .order(banner.getOrder())
                .yesNo(banner.getYesNo())
                .build();
    }

    public static List<BannerDto> of(List<Banner> banners) {

        if (banners == null) {
            return null;
        }

        List<BannerDto> bannerList = new ArrayList<>();
        for (Banner x : banners) {
            bannerList.add(BannerDto.of(x)); /*위에 있는 of 메서드 호출*/
        }

        return bannerList;
    }
}

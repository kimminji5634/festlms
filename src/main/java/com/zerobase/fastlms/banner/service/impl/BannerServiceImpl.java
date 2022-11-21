package com.zerobase.fastlms.banner.service.impl;

import com.zerobase.fastlms.banner.dto.BannerDto;
import com.zerobase.fastlms.banner.entity.Banner;
import com.zerobase.fastlms.banner.mapper.BannerMapper;
import com.zerobase.fastlms.banner.model.BannerInput;
import com.zerobase.fastlms.banner.model.BannerParam;
import com.zerobase.fastlms.banner.repository.BannerRepository;
import com.zerobase.fastlms.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor // 의존성 주입에 대해 생성자 만들어줌
@Service
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;


    @Override
    public boolean add(BannerInput parameter) {

         //LocalDate regDt = getLocalDate(parameter.getSaleEndDtText());

         Banner banner = Banner.builder()
                 .no(parameter.getNo())
                 .bannerName(parameter.getBannerName())
                 .regDt(LocalDateTime.now())
                 .open(parameter.getOpen())
                 .filename(parameter.getFilename())
                 .urlFilename(parameter.getUrlFilename())
                 .order(parameter.getOrder())
                 .yesNo(parameter.getYesNo())
                 .build();
         bannerRepository.save(banner);

        return true;
    }


    @Override
    public List<BannerDto> list(BannerParam parameter) {

        long totalCount = bannerMapper.selectListCount(parameter);
        List<BannerDto> list = bannerMapper.selectList(parameter);
        // list가 empty 아니라면
        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for (BannerDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }

        return list;
    }

    @Override
    public boolean del(String idList) { // list.html에서 해주었듯 ,(콤마) 단위의 문자열로 온 상태

        if(idList != null && idList.length() > 0) {
            String[] ids = idList.split(",");
            for (String x: ids) {
                long id = 0L;
                try {
                    id = Long.parseLong(x); // parseLong 들어가보면 예외 발생시킬 수 있음 -> try로 싸자
                } catch (Exception e) {
                }

                if (id > 0) { // 이 경우에만 삭제 로직 수행
                    bannerRepository.deleteById(id);
                }
            }
        }

        return true;
    }

}

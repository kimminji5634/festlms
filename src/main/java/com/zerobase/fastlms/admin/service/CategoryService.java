package com.zerobase.fastlms.admin.service;

import com.zerobase.fastlms.admin.dto.CategoryDto;
import com.zerobase.fastlms.admin.model.CategoryInput;

import java.util.List;
public interface CategoryService {

    List<CategoryDto> list();

    // 카테고리 신규 추가
    boolean add(String categoryName);

    // 카테고리 수정 => 가공된 데이터에 매핑되도록
    boolean update(CategoryInput parameter);

    // 카테고리 삭제
    boolean del(long id);

    // 프론트 카테고리 정보
    List<CategoryDto> frontList(CategoryDto parameter);
}

package com.zerobase.fastlms.course.mapper;

import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.model.TakeCourseParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper //xml과 매핑되는 형태로 구현됨
public interface TakeCourseMapper {

    // paging 하는 경우 아래의 두 가지 경우로 메서드를 받음
    long selectListCount(TakeCourseParam parameter);
    List<TakeCourseDto> selectList(TakeCourseParam parameter);
    List<TakeCourseDto> selectListMyCourse(TakeCourseParam parameter);
}

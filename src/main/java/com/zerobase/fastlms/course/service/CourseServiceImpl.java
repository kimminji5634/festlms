package com.zerobase.fastlms.course.service;

import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.entity.Course;
import com.zerobase.fastlms.course.entity.TakeCourse;
import com.zerobase.fastlms.course.mapper.CourseMapper;
import com.zerobase.fastlms.course.model.CourseInput;
import com.zerobase.fastlms.course.model.CourseParam;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.model.TakeCourseInput;
import com.zerobase.fastlms.course.repository.CourseRepository;
import com.zerobase.fastlms.course.repository.TakeCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor // 의존성 주입에 대해 생성자 만들어줌
@Service
public class CourseServiceImpl implements CourseService{
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final TakeCourseRepository takeCourseRepository;

    // 문자열 -> 날짜로 변경
    private LocalDate getLocalDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(value, formatter);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public boolean add(CourseInput parameter) {

         LocalDate saleEndDt = getLocalDate(parameter.getSaleEndDtText());

         Course course = Course.builder()
                 .categoryId(parameter.getCategoryId())
                 .subject(parameter.getSubject())
                 .keyword(parameter.getKeyword())
                 .summary(parameter.getSummary())
                 .contents(parameter.getContents())
                 .price(parameter.getPrice())
                 .salePrice(parameter.getSalePrice())
                 .saleEndDt(saleEndDt)
                 .regDt(LocalDateTime.now())
                 .build();
         courseRepository.save(course);

        return true;
    }

    @Override
    public boolean set(CourseInput parameter) {

        LocalDate saleEndDt = getLocalDate(parameter.getSaleEndDtText());

        Optional<Course> optionalCourse = courseRepository.findById(parameter.getId());
        if (!optionalCourse.isPresent()) {
            // 수정할 데이터 없음
            return false;
        }

        Course course = optionalCourse.get();
        course.setCategoryId(parameter.getCategoryId());
        course.setSubject(parameter.getSubject());
        course.setKeyword(parameter.getKeyword());
        course.setSummary(parameter.getSummary());
        course.setContents(parameter.getContents());
        course.setPrice(parameter.getPrice());
        course.setSalePrice(parameter.getSalePrice());
        course.setSaleEndDt(saleEndDt);
        course.setUptDt(LocalDateTime.now());
        courseRepository.save(course);

        return true;
    }

    @Override
    public List<CourseDto> list(CourseParam parameter) {

        long totalCount = courseMapper.selectListCount(parameter);
        List<CourseDto> list = courseMapper.selectList(parameter);
        // list가 empty 아니라면
        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for (CourseDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }

        return list;
    }

    @Override
    public CourseDto getById(long id) {

        return courseRepository.findById(id).map(CourseDto::of).orElse(null);
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
                    courseRepository.deleteById(id);
                }
            }
        }

        return true;
    }

    @Override
    public List<CourseDto> frontList(CourseParam parameter) {

        if (parameter.getCategoryId() < 1) {
            // 기존에는 페이징 처리라든지 이런 저런 검색때문에 매퍼를 썼지만 이번엔 매퍼없이 list 가져오겠음
            List<Course> courseList = courseRepository.findAll();

            // courseList 는 CourseDto의 형태로 바꿔줄 수 없다.. Course가 아닌 List<Course>를 받아야 함
            return CourseDto.of(courseList);
        }

        Optional<List<Course>> optionalCourses = courseRepository.findByCategoryId(parameter.getCategoryId());
        if(optionalCourses.isPresent()) {
            return CourseDto.of(optionalCourses.get());
        }

        return null;
    }

    @Override
    public CourseDto frontDetail(long id) {

        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            return CourseDto.of(optionalCourse.get());
        }

        return null;
    }

    @Override
    public ServiceResult req(TakeCourseInput parameter) {

        // ServiceResult 에 디폴트 생성자가 아닌 다른 생성자들 만들어주면 디폴트 생성자도 만들어줘야 함
        ServiceResult result = new ServiceResult();

        Optional<Course> optionalCourse = courseRepository.findById(parameter.getCourseId());
        if (!optionalCourse.isPresent()) { // course가 존재하지 않을 때
            result.setResult(false);
            result.setMessage("강좌정보가 존재하지 않습니다.");
            return result;
        }

        Course course = optionalCourse.get();

        // 이미 신청정보가 있는지 확인
        // user_id(사용자 아이디) 에 똑같은 course_id 값이 db에 있다면 처리안되도록
        // 단, status 가 취소상태로 존재하면 신청되도록

        // 상태를 여러개 가져오므로 배열로 만들어줌
        String[] statusList = {TakeCourse.STATUS_REQ, TakeCourse.STATUS_COMPLETE};

        // statusList는 Collection<List> 이고 한쪽은 배열이라 statusList가 아래와 같이 아닌 변경하여 사용
        long count = takeCourseRepository.countByCourseIdAndUserIdAndStatusIn(course.getId()
                , parameter.getUserId(), Arrays.asList(statusList));

        if (count > 0) {
            result.setResult(false);
            result.setMessage("이미 신청한 강좌 정보가 존재합니다.");
            return result;
        }

        TakeCourse takeCourse = TakeCourse.builder()
                .courseId(course.getId())
                .userId(parameter.getUserId())
                .payPrice(course.getSalePrice())
                .regDt(LocalDateTime.now())
                .status(TakeCourse.STATUS_REQ)
                .build();
        takeCourseRepository.save(takeCourse);

        result.setResult(true);
        result.setMessage("");

        return result;
    }
}

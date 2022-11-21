package com.zerobase.fastlms.course.dto;

import com.zerobase.fastlms.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseDto {

    Long id;
    long categoryId;
    String imagePath;
    String keyword;
    String subject;
    String summary;
    String contents;
    long price;
    long salePrice;
    LocalDate saleEndDt;

    LocalDateTime regDt; // 등록일(추가날짜)
    LocalDateTime uptDt; // 수정일(수정날짜)

    String filename;
    String urlFilename;

    long totalCount;
    long seq;

    /*of 메서드의 의미 : Course entity 를 받아서 CourseDto 로 리턴해줌*/
    public static CourseDto of(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .categoryId(course.getCategoryId())
                .imagePath(course.getImagePath())
                .keyword(course.getKeyword())
                .subject(course.getSubject())
                .summary(course.getSummary())
                .contents(course.getContents())
                .price(course.getPrice())
                .salePrice(course.getSalePrice())
                .saleEndDt(course.getSaleEndDt())
                .regDt(course.getRegDt())
                .uptDt(course.getUptDt())
                .filename(course.getFilename())
                .urlFilename(course.getUrlFilename())
                .build();
    }

    public static List<CourseDto> of(List<Course> courses) {

        if (courses == null) {
            return null;
        }

        List<CourseDto> courseList = new ArrayList<>();
        for (Course x : courses) {
            courseList.add(CourseDto.of(x)); /*위에 있는 of 메서드 호출*/
        }
        return courseList;
    }
}

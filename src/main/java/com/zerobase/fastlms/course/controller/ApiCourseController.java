package com.zerobase.fastlms.course.controller;

import com.zerobase.fastlms.admin.service.CategoryService;
import com.zerobase.fastlms.common.model.ResponseResult;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.model.TakeCourseInput;
import com.zerobase.fastlms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController // json body 형태로 데이터가 리턴됨
// @Controller 뷰를 리턴하게 되어 있음
public class ApiCourseController extends BaseController{
    private final CourseService courseService;
    private final CategoryService categoryService;


    // api는 뷰가 아닌 데이터를 바로 리턴해줌
    // ResponseEntity<?> 리턴값은 뷰 엔진이 아닌 문자열임!
    @PostMapping("/api/course/req.api")
    public ResponseEntity<?> courseReq(Model model
                         , @RequestBody TakeCourseInput parameter
                         ,  Principal principal) {

        // TakeCourseInput 에 있는 courseId에 대해 수강신청을 해주자
        // Principal은 제공해주는데 .getName 하면 로그인한 아이디이다
        parameter.setUserId(principal.getName());

        ServiceResult result = courseService.req(parameter);
        if (!result.isResult()) {
            ResponseResult responseResult = new ResponseResult(false, result.getMessage());
            return ResponseEntity.ok().body(responseResult);
        }

        // 수강신청에 성공한 경우
        ResponseResult responseResult = new ResponseResult(true);
        return ResponseEntity.ok().body(responseResult);
    }
}

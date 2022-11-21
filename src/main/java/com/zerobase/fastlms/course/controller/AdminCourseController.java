package com.zerobase.fastlms.course.controller;

import com.zerobase.fastlms.admin.service.CategoryService;
import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.model.CourseInput;
import com.zerobase.fastlms.course.model.CourseParam;
import com.zerobase.fastlms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j /*에러 화면에 보여주기 위해*/
@RequiredArgsConstructor
@Controller
public class AdminCourseController extends BaseController{
    private final CourseService courseService;
    private final CategoryService categoryService;


    /*MemberParam으로 넘어온 이 변수들에 대해 자동으로 매핑 해줌*/
    @GetMapping("/admin/course/list.do")
    public String list(Model model, CourseParam parameter) {

        parameter.init(); // parameter쓰기 전 유효한지 확인
        List<CourseDto> courseList = courseService.list(parameter);

        long totalCount = 0;
        if (!CollectionUtils.isEmpty(courseList)) {
            totalCount = courseList.get(0).getTotalCount();
        }
        // 바뀌기 전 String queryString = "";
        String queryString = parameter.getQueryString();
        String pagerHtml = getPagerHtml(totalCount, parameter.getPageSize(), parameter.getPageIndex(), queryString);

        model.addAttribute("list", courseList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pager", pagerHtml);

        return "admin/course/list"; // 이 뷰!!!
    }

    @GetMapping(value = {"/admin/course/add.do", "/admin/course/edit.do"})
    /*CourseInput 주입 받기*/
    public String add(Model model, HttpServletRequest request, CourseInput parameter) {

        // 카테고리 정보를 내려줘야 함
        model.addAttribute("category", categoryService.list());


        boolean editMode = request.getRequestURI().contains("/edit.do");
        CourseDto detail = new CourseDto();

        if (editMode) {
            long id = parameter.getId();
            // 데이터가 존재하면 수정하게 해주기
            CourseDto existCourse = courseService.getById(id);
            if (existCourse == null) {
                // error 처리
                model.addAttribute("message", "강좌정보가 존재하지 않습니다.");
                return "admin/common/error";
            }

            detail = existCourse;
        }
        model.addAttribute("editMode", editMode);
        model.addAttribute("detail", detail);
        return "admin/course/add"; // 이 뷰!!!
    }

    /* 파일명 가져오기 */
    private String[] getNewSaveFile(String baseLocalPath, String baseUrlPath, String originalFilename) {

        LocalDate now = LocalDate.now();

        String[] dirs = {
                String.format("%s/%d/", baseLocalPath, now.getYear()),
                String.format("%s/%d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue()),
                String.format("%s/%d/%02d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth())};

        String urlDir = String.format("%s/%d/%02d/%02d/", baseUrlPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        for (String dir : dirs) {
            File file = new File(dir); // 파일 생성
            if (!file.isDirectory()) {
                file.mkdir(); // dir 생성
            }
        }

        /*확장자 붙여야 함*/
        String fileExtension = "";
        if (originalFilename != null) {
            int dotPos = originalFilename.lastIndexOf("."); /* . 위치를 가져옴*/
            if (dotPos > - 1) {
                fileExtension = originalFilename.substring(dotPos + 1);
            }
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");/* - 제거*/
        String newFilename = String.format("%s%s", dirs[2], uuid); /*파일명임*/
        String newUrlFilename = String.format("%s%s", urlDir, uuid);
        if (fileExtension.length() > 0) {
            newFilename += "." + fileExtension;
            newUrlFilename += "." + fileExtension;
        }

       return new String[]{newFilename, newUrlFilename};
    }

    @PostMapping(value = {"/admin/course/add.do", "/admin/course/edit.do"})
    public String addSubmit(Model model, HttpServletRequest request
            , MultipartFile file
            , CourseInput parameter) {

        String saveFilename = "";
        String urlFilename = "";

        if (file != null) {
            String originalFilename = file.getOriginalFilename(); /*확장자*/
            String baseLocalPath = "/C:/dev/intelliJ_lms_project/fastlms/files";
            String baseUrlPath = "/files";

            /*밑의 주소로 사진도 옮겨주니까 인텔리제이 files(서버)에 파일 올라감*/
            String[] arrFilename = getNewSaveFile(baseLocalPath, baseUrlPath, originalFilename); /*파일이 저장되게*/

            saveFilename = arrFilename[0];
            urlFilename = arrFilename[1];

            try {
                File newFile = new File(saveFilename);
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
            } catch (IOException e) {
                log.info("################### - 1");
                log.info(e.getMessage());
            }
        }

        // setter로 받기 위해서는 parameter인 CourseInput 에서 해당 프로퍼티와 @Data 있어야함!!!!
        parameter.setFilename(saveFilename);
        parameter.setUrlFilename(urlFilename);

        boolean editMode = request.getRequestURI().contains("/edit.do");

        if (editMode) {
            long id = parameter.getId();
            // 데이터가 존재하면 수정하게 해주기
            CourseDto existCourse = courseService.getById(id);
            if (existCourse == null) {
                // error 처리
                model.addAttribute("message", "강좌정보가 존재하지 않습니다.");
                return "admin/common/error";
            }
            // edit 모드일 경우 => 수정 전용 메서드
            boolean result = courseService.set(parameter);

        } else {
            // add 모드일 경우
            boolean result = courseService.add(parameter);
        }

        return "redirect:/admin/course/list.do"; // 이 뷰!!!
    }

    @PostMapping("/admin/course/delete.do")
    public String del(Model model, HttpServletRequest request, CourseInput parameter) {

        boolean result = courseService.del(parameter.getIdList());

        return "redirect:/admin/course/list.do"; // 이 뷰!!!
    }
}

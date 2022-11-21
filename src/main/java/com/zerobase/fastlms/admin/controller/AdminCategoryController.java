package com.zerobase.fastlms.admin.controller;

import com.zerobase.fastlms.admin.dto.CategoryDto;
import com.zerobase.fastlms.admin.model.CategoryInput;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.admin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j /*에러 화면에 보여주기 위해*/
@RequiredArgsConstructor
@Controller
public class AdminCategoryController {

    private final CategoryService categoryService;

    /*MemberParam으로 넘어온 이 변수들에 대해 자동으로 매핑 해줌*/
    @GetMapping("/admin/category/list.do")
    public String list(Model model, MemberParam parameter) {

        List<CategoryDto> list = categoryService.list();
        model.addAttribute("list", list);

        return "admin/category/list"; // 이 뷰!!!
    }

    @PostMapping("/admin/category/add.do")
    public String add(Model model, CategoryInput parameter) {

        boolean result = categoryService.add(parameter.getCategoryName()); // get 가져오기

        return "redirect:/admin/category/list.do";
    }

    @PostMapping("/admin/category/delete.do")
    public String del(Model model, CategoryInput parameter) {

        boolean result = categoryService.del(parameter.getId());

        return "redirect:/admin/category/list.do";
    }

    @PostMapping("/admin/category/update.do")
    public String update(Model model, CategoryInput parameter) {

        boolean result = categoryService.update(parameter);

        return "redirect:/admin/category/list.do";
    }
}

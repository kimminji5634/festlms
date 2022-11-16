package com.zerobase.fastlms.util;

public class PageUtilTest {

    public static void main(String[] args) {
        // pageIndex : 현재 페이지 번호
        PageUtil pageUtil = new PageUtil(151, 10, 3, "");
        String htmlPager = pageUtil.pager();

        System.out.println(htmlPager);
    }
}

package com.zerobase.fastlms.course.model;

import com.zerobase.fastlms.admin.model.CommonParam;
import lombok.Data;

@Data
public class ServiceResult extends CommonParam {
    boolean result; // true 를 리턴하는가? false 를 리턴하는가?
    String message; // 에러 메시지는 무엇인가

    public ServiceResult(){
        result = true; // 디폴트 값 true로 구현
    }

    public ServiceResult(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public ServiceResult(boolean result) {
        this.result = result;
    }
}

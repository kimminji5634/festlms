package com.zerobase.fastlms.member.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data // getter, setter 만들어줌
public class ResetPasswordInput {

    private String userId;
    private String userName;

    private String id; // reset 시 웹 주소 id 값을 말함
    private String password;
}

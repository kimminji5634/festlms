package com.zerobase.fastlms.member.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data // getter, setter 만들어줌
public class MemberInput {

    private String userId;
    private String userName;
    private String password;
    private String phone;

    private String newPassword;

    private String zipcode; // html name 과 일치해야함
    private String addr;
    private String addrDetail;





}

package com.zerobase.fastlms.admin.model;

import lombok.Data;

@Data // CategoryInput을 다른데서 get, set 으로 받기 위해서 필요
public class CategoryInput {

    long id;
    String categoryName;
    int sortValue;
    boolean usingYn;
}

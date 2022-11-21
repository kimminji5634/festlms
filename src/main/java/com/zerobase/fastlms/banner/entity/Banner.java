package com.zerobase.fastlms.banner.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder //빌더 추가하려면 NoArgs... AllArgs도 추가해야 함
@Data
@Entity
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long no;

    String bannerName;
    LocalDateTime regDt; // 등록일(추가날짜)

    String filename;
    String urlFilename;

    // 배너 등록
    String open;
    int order;
    String yesNo;
}

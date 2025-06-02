package com.dev.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "동향보고 DTO")
public class ReportDTO {
	
	private String idxDate;				//yyyymmdd 8자리
    private String controlRepoter;		//방제주요 활동사항 보고자
    private String controlContent;		//방제주요 활동사항 내용
    private String mainRepoter;			//주요 정보사항 보고자
    private String mainContent;			//주요 정보사항 내용
    private String troubleRepoter;		//사건 사고 발생 보고자
    private String troubleContent;		//사건 사고 발생 내용
    private String praiseRepoter;		//사건 사고 예방 및 칭찬사례 보고자
    private String praiseContent;		//사건 사고 예방 및 칭찬사례 내용
    private String etcContent;			//비고 (기타사항)

    private String writeUserId;			//작성자
    private String deptCd;				//부서코드
    //private String writeDeptCd;
    
    //에디터용 새로 추가
    private String contentMode; 		//Template or Editor 구분 flag (T 또는 E)
    private String htmlContent; 		//에디터 전체 HTML
    
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime writeDate;

    
    private BigDecimal beforeTo;
    private BigDecimal beforePo;
    private BigDecimal beforeToDabi;
    private BigDecimal beforePoDabi;
    private BigDecimal beforeTo2;
}

package com.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "사용자 DTO")
public class UserDTO {
	
	private String userId = "momo";
    private String userNm = "경영정보팀";
    private String userFg;
    private String logPwd;
    private String mail;
    private String deptCd;
    private String deptNm;
    private String titleCd;
    private String titleNm;
    private String dutyCd;
    private String dutyNm;
    private String titleSort;
    private String dutySort;
    private String useFg;
    private String empNo;
    private String comCd;
    private String comNm;
    private String readPms;
    private String writePms;
    private String salePms;
    
}

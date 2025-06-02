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
@Schema(description = "부서코드 DTO")
public class DeptDTO {

	private String deptCd;
	private String deptNm;
	private String deptLevel;
	private String parentCd;
	private String deptSort;
	private String deptCdPath;
	private String deptNmPath;
	private String useFg;
	private String adFg;
	private String comCd;
	private String comNm;
	private String insaCd;
	
}
package com.dev.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dev.dto.DeptDTO;

@Mapper
public interface DeptMapper {

	//모사
	List<DeptDTO> selectRootDeptList(@Param("userId") String userId, 
									@Param("deptNm") String deptNm, 
									@Param("mainDeptNm") String mainDeptNm,
									@Param("titleCd") String titleCd);
	//계열사
	List<DeptDTO> selectDeptList(@Param("userId") String userId, 
								@Param("deptNm") String deptNm, 
								@Param("mainDeptNm") String mainDeptNm,
								@Param("titleCd") String titleCd);	
}
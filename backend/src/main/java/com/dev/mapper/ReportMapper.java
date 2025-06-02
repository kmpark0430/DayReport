package com.dev.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dev.dto.ReportDTO;

@Mapper
public interface ReportMapper {

	ReportDTO getReport(@Param("idxDate") String idxDate, @Param("deptCd")  String deptCd);
	
	ReportDTO getRecentReport(String deptCd);
	
    int beforeSaveCountCheck (@Param("idxDate") String idxDate, @Param("deptCd")  String deptCd);
    
	int insert(ReportDTO dto);
	
	int update(ReportDTO dto);

}

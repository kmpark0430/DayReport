package com.dev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dto.ReportDTO;
import com.dev.mapper.ReportMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
	
	private final ReportMapper reportMapper;
	
	public ReportDTO getReport(String idxDate, String deptCd) {
		
		ReportDTO report = reportMapper.getReport(idxDate, deptCd);
		log.info("@Service returned report: {}", report);
		return report;
	}
	
	public ReportDTO getRecentReport(String deptCd) {
		ReportDTO recent = reportMapper.getRecentReport(deptCd);
		return recent;
	}

	@Transactional
	public void save(ReportDTO dto, boolean isUpdate) {
	    // 선택한 날짜와 부서코드로 이미 등록된 보고서가 있는지 체크
	    int cnt = reportMapper.beforeSaveCountCheck(dto.getIdxDate(), dto.getDeptCd());
	    
	    if (!isUpdate) {
	    	if (cnt > 0) throw new IllegalStateException("이미 등록된 보고서가 있습니다.");
	    	reportMapper.insert(dto);
	    } else {
	    	// 수정시 레코드가 없으면 에러
	    	if (cnt == 0) throw new IllegalStateException("수정할 보고서가 존재하지 않습니다.");
	    	reportMapper.update(dto);
	    }
	}
	
	
}

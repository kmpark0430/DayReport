package com.dev.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dto.ReportDTO;
import com.dev.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/report")
public class ReportController {
	
	private final ReportService reportService;
	
	@Operation( summary = "부서별 동향 보고 데이터 조회",
				description = "주어진 `idxDate`(YYYYMMDD)와 `deptCd`에 해당하는 보고서 데이터를 반환함")
	@GetMapping("/{idxDate}/{deptCd}")
	public ResponseEntity<ReportDTO> getReport(@PathVariable("idxDate") String idxDate, @PathVariable("deptCd") String deptCd) {
		log.info("@Controller getReport idxDate={}, deptCd={}", idxDate, deptCd);
		ReportDTO report = reportService.getReport(idxDate, deptCd);
		return report != null 
				? ResponseEntity.ok(report)
				: ResponseEntity.notFound().build();
    }
	
	@Operation( summary = "해당 부서의 최근 작성된 보고서 데이터 조회 (날짜와 관계없음)",
			description = "`deptCd`로 조회된 최근 등록된 보고서 데이터를 반환함(T or E)")
	@GetMapping("/{deptCd}")
	public ResponseEntity<ReportDTO> getRecentReport(@PathVariable("deptCd") String deptCd) {
		log.info("@getRecentReport deptCd={}", deptCd);
		ReportDTO recent = reportService.getRecentReport(deptCd);
		
		return ResponseEntity.ok(recent);
	}
	
	@Operation( summary = "보고서 신규 등록",
			description = "요청 페이로드에 포함된 보고서 정보를 새로 저장함")
    @PostMapping
    public ResponseEntity<ReportDTO> insert(@RequestBody @Valid ReportDTO dto) {
    	log.info("@createReport payload={}", dto);
    	reportService.save(dto, false);	//false = insert 모드
        return ResponseEntity.ok(dto);
    }

	@Operation( summary = "보고서 수정",
			description = "주어진 `idxDate`와 `deptCd`에 해당하는 보고서를 요청 페이로드의 정보로 업데이트")
    @PutMapping("/{idxDate}/{deptCd}")
    public ResponseEntity<ReportDTO> update(@PathVariable("idxDate") String idxDate, @PathVariable("deptCd") String deptCd,
    										@RequestBody @Valid ReportDTO dto) {
        dto.setIdxDate(idxDate);
    	dto.setDeptCd(deptCd);
        log.info("@@ updateReport payload={}", dto);
        reportService.save(dto, true);	//true = update 모드
        return ResponseEntity.ok(dto);
    }
    
}
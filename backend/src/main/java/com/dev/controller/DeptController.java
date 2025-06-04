package com.dev.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dto.DeptDTO;
import com.dev.service.DeptService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/dept")
public class DeptController {
	
	private final DeptService deptService;
	
    @Operation( summary = "메뉴의 모사 or 계열사별 부서코드 가져옴",
    	      	description = "쿼리 파라미터 `type` 에 '모사' 또는 '계열사' 를 넣어 호출")
    
	@GetMapping("/getCode")
	public ResponseEntity<List<DeptDTO>> getDeptCodeList(@RequestParam(name = "type", required = true) String type) {
	    // type이 null 또는 빈 문자열인 경우 400
	    if (type == null || type.isBlank()) {
	        return ResponseEntity.badRequest().build();
	    }
	    List<DeptDTO> result = deptService.getCodeByType(type);
	    return ResponseEntity.ok(result);
	}
    
}
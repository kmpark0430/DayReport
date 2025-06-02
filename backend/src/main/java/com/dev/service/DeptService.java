package com.dev.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.dev.dto.DeptDTO;
import com.dev.dto.UserDTO;
import com.dev.mapper.DeptMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeptService {
	
	private final DeptMapper deptMapper;
	
	@ModelAttribute("loginUser")
	public UserDTO userDTO() {
		return new UserDTO();
	}
	
    private final String userId = userDTO().getUserId();
	private final String deptNm = userDTO().getDeptNm();
	private final String[] arrDeptNm = deptNm != null ? deptNm.split("-") : new String[0];    
	private final String mainDeptNm = arrDeptNm.length > 0 ? arrDeptNm[0] : "";                 
	private final String titleCd = "1"; //아무거나 넣음
	
	public List<DeptDTO> getCodeByType(String type) {
		if ("모사".equals(type)) {
            return deptMapper.selectRootDeptList(this.userId, deptNm, mainDeptNm, titleCd);
            
        } else if ("계열사".equals(type)) {
            return deptMapper.selectDeptList(this.userId, deptNm, mainDeptNm, titleCd);
            
        } else {
            return Collections.emptyList();
        }
	}
	
}
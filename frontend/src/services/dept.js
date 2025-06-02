// src/services/dept.js
// 부서 API 정의
import api from './apiClient';

/**
 * 모사/계열사별 부서 코드 목록 조회
 * @param {string} type  '모사' 또는 '계열사'
 * @returns {Promise<DeptDTO[]>}
 */
export function fetchDeptCodes(type) {
  // 실제 호출: GET /api/dept/getCode?type={type}
  return api.get('/dept/getCode', { params: { type } });
}

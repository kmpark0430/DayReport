// src/services/report.js
// 레포트 API 정의
import api from './apiClient';

/**
 * 보고서 조회
 * @param {string} idxDate 8자리 날짜(YYYYMMDD)
 * @param {string} deptCd  부서 코드
 * @returns {Promise<ReportDTO>}
 */
export async function fetchReport(idxDate, deptCd) {
  const data = await api.get(`/report/${idxDate}/${deptCd}`);
  console.log("@ 보고서 조회 fetchReport data: ", data)
  return data; // interceptor가 이미 response.data를 주니까
}

/**
 * 최근 보고서 데이터 조회
 * @param {string} deptCd  부서 코드
 * @returns {Promise<ReportDTO>}
 */
export async function recentReport(deptCd) {
  const data = await api.get(`/report/${deptCd}`);
  console.log("@ recentReport 반환값: ", data)
  return data; // interceptor가 이미 response.data를 주니까
}

/**
 * 새 보고서 등록
 * @param {ReportDto} reportPayload
 * @returns {Promise<ReportDto>}
 */
export function createReport(reportPayload) {
  return api.post('/report', reportPayload);
}

/**
 * 기존 보고서 수정
 * @param {string} deptCd
 * @param {ReportDto} reportPayload
 * @returns {Promise<ReportDto>}
 */
export function updateReport(idxDate, deptCd, reportPayload) {
  return api.put(`/report/${idxDate}/${deptCd}`, reportPayload);
}

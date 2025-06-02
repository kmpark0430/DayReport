// src/services/apiClient.js
// 공통 axios 설정
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 5000,
  // timeout, headers, interceptors 등 설정 위치  
  // 추후 인증·로더·인터셉터를 추가할 계획이 있다면 분리하는게 좋다고 함
});

api.interceptors.response.use(res => res.data)  // 응답 인터셉터: 항상 response.data만 꺼내서 리턴

export default api;

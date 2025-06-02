// src/plugins/vuetify.js
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import { aliases, mdi } from 'vuetify/iconsets/mdi';
import '@mdi/font/css/materialdesignicons.css'

export default createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: { mdi },
  },
  theme: {
    defaultTheme: 'modernTheme',
    themes: {
      modernTheme: {
        dark: false,     // 라이트 모드
        colors: {
          // 기본 색상
          primary:   '#001F3F',  // 네이비
          secondary: '#37474F',  // 다크 그레이
          accent:    '#E91E63',  // 모던 핑크
          background:'#F5F5F5',  // 페이지 배경
          surface:   '#FFFFFF',  // 카드·네비게이션 표면
          // 상태 색상
          error:     '#B00020',
          info:      '#2196F3',
          success:   '#4CAF50',
          warning:   '#FB8C00',
        }
      }
    }
  }
});

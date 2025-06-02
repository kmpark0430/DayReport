import { defineConfig } from 'vite'
import vue     from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import path    from 'path'

export default defineConfig({
  plugins: [
    vue(),
    vuetify({ autoImport: true }),
  ],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost',       // 실제 백엔드 주소
        changeOrigin: true,               // 헤더 상 Origin 을 target 으로 바꿔 줌
        rewrite: path => path.replace(/^\/api/, ''),
      },
    },
  },
})
// proxy 설정을 해두면 spring 메소드 위에 '@CrossOrigin(origins = "*")' 안써도됨 앞으로 axios.get('/api/dept') 이런 형식으로 연결
// resolve: @는 보통 src 폴더의 별칭(alias)으로 많이 쓰이는데, Vite는 기본 제공하지 않음
// 그래서 직접 resolve.alias에 명시해줘야 @/api/dept 같은 경로를 찾을 수 있음
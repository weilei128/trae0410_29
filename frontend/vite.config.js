import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 10023,
    proxy: {
      '/api': {
        target: 'http://localhost:10022',
        changeOrigin: true
      }
    }
  }
})

<!-- src/components/ReportView.vue -->
<template>
  <v-card flat v-if="report">
    <v-card-text>

      <table cellspacing="0" cellpadding="0" class="mb-2">
        <tbody>
          <tr>
            <td>
              등록일시 : {{ report.writeDate.replace(/\.\d+$/, '') }}
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 에디터 모드: HTML 테이블 전체 렌더링 -->
      <div v-if="report.contentMode === 'E'">
        <div
          style="width:100%; overflow-x:auto;"
          v-html="sanitize(report.htmlContent)"
        >
        </div>
      </div>

      <!-- TEMPLATE 모드: A/B/C/D 합쳐서 하나의 테이블로 렌더링 -->
      <table class="navy-table" v-else>
        <thead>
          <tr>
            <th>구분</th>
            <th >보고자</th>
            <th>주요내용</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in allItems" :key="item.type">
            <td>{{ item.type }}</td>
            <td v-html="sanitize(item.reporter)"></td>
            <td v-html="sanitize(item.content)" style="padding-left: 16px; text-align: left;"></td>
          </tr>
        </tbody>
      </table>

    </v-card-text>
  </v-card>

  <div v-else class="no-data">
    해당 날짜에 데이터가 없습니다.
  </div>
</template>

<script setup>
import { computed } from 'vue'
import DOMPurify from 'dompurify'

const props = defineProps({
  report: {
    type: Object,
    required: false
  }
})

// A/B/C 항목을 한 배열로 합치기
const allItems = computed(() => {
  if (!props.report) return []    //props.report가 없으면
  return [
    {
      type: '방제 주요 활동사항',
      reporter: props.report.controlRepoter,
      content: props.report.controlContent
    },
    {
      type: '주요 정보사항',
      reporter: props.report.mainRepoter,
      content: props.report.mainContent
    },
    {
      type: '사건 사고 발생',
      reporter: props.report.troubleRepoter,
      content: props.report.troubleContent
    },
    {
      type: '사건 사고 예방 및 칭찬사례',
      reporter: props.report.praiseRepoter,
      content: props.report.praiseContent
    },
    {
      type: '비고 (기타사항)',
      content: props.report.etcContent
    }
  ]
})

// XSS 방어용 sanitize 헬퍼
function sanitize(html) {
  return html ? DOMPurify.sanitize(html) : ''
}
</script>


<style>
.navy-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e0e0e0;
  font-size: 15px;
}

/* 헤더 & 셀 모두에 1px 실선 테두리 */
.navy-table th,
.navy-table td {
  border: 1px solid #e0e0e0;
  padding: 12px;
  text-align: center;
  vertical-align: top !important;
}

.navy-table th {
  background-color: #002147;
  color: #ffffff;
}

.navy-table tbody tr:hover {
  background-color: rgba(0, 33, 71, 0.05);
}

/* 열 너비 고정 */
.navy-table th:nth-child(1),
.navy-table td:nth-child(1) { width: 15%; }
.navy-table th:nth-child(2),
.navy-table td:nth-child(2) { width: 15%; }
.navy-table th:nth-child(3),
.navy-table td:nth-child(3) { width: 70%; }
/* 주요내용(3열) td만 왼쪽 정렬 */
.navy-table td:nth-child(3) {
  text-align: left !important;
  padding-left: 16px;
  padding-right: 16px;
}

/* v-html 로 주입된 DOM에도 동일 적용 */
::v-deep .navy-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e0e0e0;
}
::v-deep .navy-table th,
::v-deep .navy-table td {
  border: 1px solid #e0e0e0;
  padding: 12px;
  text-align: center;
  vertical-align: middle;
}
::v-deep .navy-table th {
  background-color: #002147;
  color: #ffffff;
}
::v-deep .navy-table tbody tr:hover {
  background-color: rgba(0, 33, 71, 0.05);
}
::v-deep .navy-table th:nth-child(1),
::v-deep .navy-table td:nth-child(1) { 
  width: 15%;
}
::v-deep .navy-table th:nth-child(2),
::v-deep .navy-table td:nth-child(2) { 
  width: 15%;
}
::v-deep .navy-table th:nth-child(3),
::v-deep .navy-table td:nth-child(3) { 
  width: 70%;
}
::v-deep .navy-table td:nth-child(3) {
  text-align: left
}

/* 데이터 없을 때 메시지 */
.no-data {
  text-align: center;
  padding: 400px !important;
}
</style>

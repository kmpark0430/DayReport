<!-- src/components/Layout.vue -->
<template>
  <v-app>
    <!-- App Bar (헤더) -->
    <v-app-bar 
      app 
      clipped-left 
      dark 
      color="primary" 
      height="50"
      class="v-app-bar"
    >
      <v-toolbar-title class="d-flex align-center">
        <a href="/">
          <v-img
            src="/stec_logo_white4.png"
            alt="logo"
            style="width:200px;margin-left:40px;"
            contain
            eager
          />
        </a>
      </v-toolbar-title>

      <v-spacer />

      <v-btn class="mr-2" variant="outlined" color="white" @click="openRegister" prepend-icon="mdi-file-edit">동향 보고 관리</v-btn>

      <v-btn class="mr-12" variant="outlined" color="white" href="http://sgw.s-tec.co.kr" target="_blank" prepend-icon="mdi-open-in-new">
        그룹웨어로 이동
      </v-btn>
    </v-app-bar>

    <!-- Navigation Drawer (왼쪽 메뉴) -->
    <v-navigation-drawer
      app
      permanent
      clipped
      width="340"
      class="v-navigation-drawer"
    >
      <div class="drawer-content pa-0">
        <!-- Date Picker -->
        <div class="calendar-wrapper">
          <v-date-picker
            v-model="selectedDate"
            show-current
            density="compact"
            color="primary"
            width="100%"
          >
            <template #title />
            <template #default />
            <template #header>
              <div class="v-picker-title-div">
                <v-btn
                  variant="outlined"
                  color="white"
                  density="compact"
                  prepend-icon="mdi-calendar"
                  @click="goToday"
                >
                  TODAY
                </v-btn>
              </div>
            </template>
          </v-date-picker>
        </div>

        <!-- 모사/계열사 토글: 등록 모드이면 숨김 -->
        <div v-if="!isRegistering" class="toggle-wrapper pa-2">
          <v-btn-toggle v-model="selectedCompany" mandatory>
            <v-btn 
              v-for="tab in companyTabs" 
              :key="tab" 
              small
            >
            <v-icon class="me-1">
              {{ tab === '모사' ? 'mdi-home-variant' : 'mdi-domain' }}
            </v-icon>
              {{ tab }}
            </v-btn>
          </v-btn-toggle>
        </div>

        <!-- 부서 리스트: 등록 모드이면 숨김 -->
        <div v-if="!isRegistering" class="dept-list pa-2">
          <v-list nav dense>
            <v-list-item
              v-for="dept in departments"
              :key="dept.deptCd"
              @click="selectDept(dept.deptCd)"
              :active="dept.deptCd === selectedDept"
            >
              <v-list-item-title>{{ dept.deptNm }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </div>
      </div>
    </v-navigation-drawer>

    
    <!-- Main Content -->
    <v-main>
      <!-- Breadcrumbs: 조회 or 등록 모드 모두 날짜를 반영 -->
      <v-sheet class="breadcrumbs-wrapper custom-bc" tile>
        <v-breadcrumbs class="px-4" divider=">">
          <v-breadcrumbs-item>
            {{ breadcrumbsLabel }}
          </v-breadcrumbs-item>
        </v-breadcrumbs>
      </v-sheet>

      <!-- ReportView 영역 -->
      <v-container fluid class="pa-0">
        <!-- 조회 화면 -->
        <ReportView
          v-if="!isRegistering"
          :report="reportData"
        />

        <!-- 등록 화면 -->
        <RegistrationForm
          v-else
          :idxDate="formIdxDate"
          :deptCd="formDeptCd"
          :isEdit="reportData !== null"
          @saved="onSaved"
          @cancel="cancelRegister"
        />
      </v-container>      
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import dayjs from 'dayjs'
import { fetchDeptCodes } from '@/services/dept'
import { fetchReport } from '@/services/report'
import ReportView from './ReportView.vue'
import RegistrationForm from './RegistrationForm.vue'

// --- 구분 탭
const companyTabs     = ['모사', '계열사']
const selectedCompany = ref(0)

// YYYY-MM-DD 문자열 (v-date-picker 바인딩)
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))

// 부서 코드
const departments  = ref([])
const selectedDept = ref('')

// 등록 화면 토글용
const isRegistering = ref(false)
const formIdxDate   = ref('')  // 등록 폼에 보낼 idxDATE(yyyymmdd)
const formDeptCd    = ref('')  // 등록 폼에 보낼 deptCd

// --- 부서목록 로드
async function loadDepartments() {
  const type = companyTabs[selectedCompany.value]
  const list = await fetchDeptCodes(type)
  departments.value = list.map(d => ({ ...d, isNew: false }))
  selectedDept.value = list[0]?.deptCd || ''
}

// --- 보고서 로드
const reportData = ref(null)
async function loadReport() {
  if (!selectedDept.value) return;

  // selectedDate.value 가 문자열인지 Date 객체인지 알아서 처리
  const raw = selectedDate.value
  const asString = typeof raw === 'string'
    ? raw
    : dayjs(raw).format('YYYY-MM-DD');
  const idxDate = asString.replace(/-/g, '')   // "YYYYMMDD"

  try {
    reportData.value = await fetchReport(idxDate, selectedDept.value)
  } catch (err) {
    if (err.response?.status === 404) reportData.value = null
    else console.error(err)
  }
}

// --- 오늘 날짜로 이동
function goToday() {
  selectedDate.value = dayjs().format('YYYY-MM-DD')
}

// --- 동향보고 등록 화면 열기 (selectedDate 기준)
function openRegister() {
  // 1) selectedDate.value 가 Date 객체거나 DayJS 인스턴스일 수도 있으니 문자열로 포맷
  const raw = selectedDate.value
  const dateStr = typeof raw === 'string'
    ? raw
    : dayjs(raw).format('YYYY-MM-DD')

  // 2) "YYYY-MM-DD" → "YYYYMMDD"
  formIdxDate.value = dateStr.replace(/-/g, '')

  formDeptCd.value    = selectedDept.value
  isRegistering.value = true
}

// --- 등록 취소
function cancelRegister() {
  isRegistering.value = false
}

// --- 저장 후 조회 화면으로 복귀
function onSaved(returnedDto) {
  isRegistering.value = false

  // savedDto가 undefined일 수도 있으므로 체크
  if (returnedDto && returnedDto.idxDate) {
    const d = returnedDto.idxDate;
    selectedDate.value = `${d.slice(0,4)}-${d.slice(4,6)}-${d.slice(6,8)}`;   // YYYYMMDD -> YYYY-MM-DD
    selectedDept.value = returnedDto.deptCd;
  }

  loadReport(); //저장 후 조회로 복귀
}

// --- 부서 선택
function selectDept(code) {
  selectedDept.value = code
}

// ── computed: breadcrumbsLabel (조회 vs 등록) ──
const breadcrumbsLabel = computed(() => {
  // 등록 모드면 formIdxDate, 아니면 reportData.writeDate
  const rawDate = isRegistering.value
    ? formIdxDate.value
    : (reportData.value?.writeDate?.split(' ')[0].replace(/-/g,'') || '')
  if (!/^\d{8}$/.test(rawDate)) return ''
  const y = rawDate.slice(0,4)
  const m = rawDate.slice(4,6)
  const d = rawDate.slice(6,8)
  const name = departments.value.find(d=>d.deptCd===selectedDept.value)?.deptNm || ''
  const label = isRegistering.value ? '동향보고 등록' : '운영동향보고'
  return `${y}년 ${m}월 ${d}일 ${name} ${label}`
})


// watch & lifecycle
// selectedCompany 바뀌면 부서 리스트 로드 → 보고서 로드
watch(selectedCompany, () => loadDepartments().then(loadReport))

// selectedDate, selectedDept 바뀌면 보고서 로드
watch([selectedDate, selectedDept], loadReport)

// selectedDate(YYYY-MM-DD) 바뀔 때, 등록 모드면 formIdxDate 동기화
watch(selectedDate, (val) => {
  if (!isRegistering.value) return

  // Date 객체면 포맷, 아니면 그대로
  const dateStr = typeof val === 'string'
    ? val
    : dayjs(val).format('YYYY-MM-DD')

  formIdxDate.value = dateStr.replace(/-/g, '')
})

// 초기 마운트 시 부서 + 보고서 로드
onMounted(async () => {
  await loadDepartments()
  await loadReport()
})
</script>


<style scoped>
  /* AppBar 높이 지정 */
  .v-app-bar { --v-app-bar-height: 40px }
  .v-toolbar-title { line-height: 40px }
  .v-app-bar .v-btn { position: static !important; margin-left: 8px }

  /* Breadcrumbs */
  .breadcrumbs-wrapper { height: 40px; display: flex; align-items: center }
  .custom-bc { background-color: #d1d1d1 !important; box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24) }

  /* DatePicker TODAY 버튼 높이 동일화 */
  .calendar-wrapper .v-picker-title-div { height: 40px; display: flex; justify-content: flex-end; align-items: center; padding: 0 12px }

  /* 모사/계열사 토글 균등 분할 */
  .toggle-wrapper .v-btn-toggle { display: flex }
  .toggle-wrapper .v-btn        { flex: 1 }

  /*토글 pa-0, border 추가*/
  .toggle-wrapper {
    padding: 0 !important;
    border: 1px solid #ccc !important;
  }

  /* drawer-content 내부 스크롤 유지 */
  .drawer-content { display: flex; flex-direction: column; height: 100% }
  .dept-list     { flex: 1; overflow-y: auto }
</style>
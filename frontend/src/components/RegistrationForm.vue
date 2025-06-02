<!-- src/components/RegistrationForm.vue -->
<template>
  <v-card>

    <!-- 툴바: 모드 토글 / 취소 / 저장 -->
    <v-toolbar dense flat color="primary">
      <v-btn icon @click="$emit('cancel')">
        <v-icon>mdi-close</v-icon>
      </v-btn>
      <v-toolbar-title class="white--text">
        {{ isEdit ? '보고서 수정' : '보고서 등록' }}
      </v-toolbar-title>
      <v-spacer/>

      <div>
        <!-- 모드 선택: Template / Editor -->
        <v-btn-toggle v-model="form.contentMode" mandatory>
          <v-btn value="T">템플릿</v-btn>
          <v-btn value="E">에디터</v-btn>
        </v-btn-toggle>
      </div>

      <v-spacer/>

      <div class="vbtn">
        <v-btn class="mr-4" text @click="loadRecent" variant="outlined" color="white" prepend-icon="mdi-history">최근 데이터 가져오기</v-btn>
        <v-btn class="save-btn" @click="submit" variant="outlined" color="white" prepend-icon="mdi-content-save">저장</v-btn>
      </div>
    </v-toolbar>

    <!-- 본문 -->
    <v-card-text>
      <v-form ref="formRef">

        <!-- TEMPLATE 모드 -->
        <div v-if="form.contentMode === 'T'">
            <table class="navy-table">
                <thead>
                    <tr>
                    <th style="width:15%;">구분</th>
                    <th style="width:15%;">보고자</th>
                    <th style="width:70%;">주요 내용</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in items" :key="item.key">
                    <td>{{ item.label }}</td>
                    <!-- 보고자 -->
                    <td>
                      <div
                        class="editable-content reporter"
                        contenteditable="true"
                        v-html="form[item.key + 'Repoter']"
                        @blur="onTemplateRepoterBlur(item.key, $event)"
                      ></div>
                    </td>
                    <!-- 주요내용-->
                    <td>
                      <div
                      class="editable-content"
                      contenteditable="true"
                      v-html="form[item.key + 'Content']"
                      @blur="onTemplateContentBlur(item.key, $event)"
                      ></div>
                    </td>

                    </tr>
                    <tr>
                    <td>비고</td>
                    <td colspan="2">
                      <div
                        class="editable-content"
                        contenteditable="true"
                        v-html="form.etcContent"
                        @blur="onTemplateEtcBlur($event)"
                      ></div>
                    </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- EDITOR 모드 -->
        <div v-else>
          <!-- editor 인스턴스가 있을 때만 렌더 -->
          <div v-if="editor">
            <EditorContent
            :editor="editor"
            class="editor"
            />
          </div>
        </div>

      </v-form>
    </v-card-text>

  </v-card>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, watch } from 'vue'
import { Editor, EditorContent }       from '@tiptap/vue-3'
import StarterKit                       from '@tiptap/starter-kit'
import Table                            from '@tiptap/extension-table'
import TableRow                         from '@tiptap/extension-table-row'
import TableCell                        from '@tiptap/extension-table-cell'
import TableHeader                      from '@tiptap/extension-table-header'
import { fetchReport, recentReport, createReport, updateReport } from '@/services/report'

const editorTableHtml = `
  <table class="navy-table">
    <thead>
      <tr>
        <th>구분</th>
        <th>보고자</th>
        <th>주요 내용</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>방제 주요 활동사항</td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>주요 정보사항</td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>사건 사고 발생</td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>사건 사고 예방 및 칭찬사례</td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>비고 (기타사항)</td>
        <td></td>
        <td></td>
      </tr>
    </tbody>
  </table>
`

const props = defineProps({
  idxDate:  { type: String, required: true },
  deptCd:   { type: String, required: true },
  isEdit:   { type: Boolean, default: false }  // 수정 모드 구분
})

const emit = defineEmits(['saved','cancel'])
const formRef = ref(null)

// A/B/C/D 항목 메타
const items = [
  { key:'control', label:'방제 주요 활동사항' },
  { key:'main',    label:'주요 정보사항' },
  { key:'trouble', label:'사건 사고 발생' },
  { key:'praise',  label:'사건 사고 예방 및 칭찬사례' },
  { key:'etc',  label:'비고 (기타사항)' },
]

// reactive form 객체 초기화
const form = reactive({
  idxDate:      props.idxDate,
  deptCd:       props.deptCd,
  contentMode:  'E',      // 초기 모드 - 에디터

  // 에디터 필드
  htmlContent:'',

  // 템플릿 필드
  controlRepoter:'', controlContent:'',
  mainRepoter:'',    mainContent:'',
  troubleRepoter:'', troubleContent:'',
  praiseRepoter:'',  praiseContent:'',
  etcContent:''
})

// TipTap 에디터 인스턴스 생성
const editor = new Editor({
  content: (
      // 등록 모드 + Editor 모드면 뼈대 HTML 불러옴, 아니면 form.htmlContent
      (!props.isEdit && form.contentMode === 'E')
      ? editorTableHtml
      : form.htmlContent
  ),
  extensions: [
      StarterKit,
      Table.configure({
        resizable: false,
        HTMLAttributes: {
          class: 'navy-table'
        }
      }),
      TableRow,
      TableHeader,
      TableCell,
  ],
  onUpdate: ({ editor }) => {
      form.htmlContent = editor.getHTML()
      form.htmlContent = stripMinWidth(form.htmlContent)
  }
})


// HTML 문자열에서 모든 min-width 인라인 스타일을 제거하는 헬퍼
// <table style="…min-width:…"> 또는는 <col style="…min-width:…"> 제거
function stripMinWidth(html) {
  return html
    .replace(/(<table[^>]*?)\sstyle="[^"]*?min-width:[^"]*?;?[^"]*?"/gi, '$1')
    .replace(/(<col[^>]*?)\sstyle="[^"]*?min-width:[^"]*?;?[^"]*?"/gi, '$1')
}


// 입력하는 도중 커서가 튕겨서 form을 바꾸지 않고, blur 시에만 업데이트하는 것으로 바꿈
// 템플릿 모드에서 '보고자' 편집 이벤트 처리 함수
// key: 'control', 'main', 'trouble', 'praise' 중 하나
// evt: 실제 input 이벤트 객체, evt.target.innerHTML에 편집된 HTML 전체가 담김
// “보고자”에서 focus가 빠질 때(blur) 호출
function onTemplateRepoterBlur(key, evt) {
  // evt.target.innerHTML: 최종 편집된 HTML
  form[key + 'Repoter'] = evt.target.innerHTML;
  // 한 번만 바뀌기 때문에 v-html 리렌더링도 여기서 딱 한 번만 발생
}

// “주요 내용”에서 focus가 빠질 때(blur) 호출
function onTemplateContentBlur(key, evt) {
  form[key + 'Content'] = evt.target.innerHTML;
}

// “비고”에서 focus가 빠질 때(blur) 호출
function onTemplateEtcBlur(evt) {
  form.etcContent = evt.target.innerHTML;
}
//

onMounted(async () => {
  try {
    if (props.isEdit) {
      // 수정 모드라면 기존 데이터를 가져와 form에 세팅
      console.log('수정모드에서 fetchReport 호출 ===>')
      const dto = await fetchReport(form.idxDate, form.deptCd)
      form.contentMode = dto.contentMode    // mode 대로 초기화

      if (dto.contentMode === 'T') {
        items.forEach(({ key }) => {
          if (key === 'etc') {
          // etc는 reporter 필드가 없으므로 content만 세팅
          form.etcContent = dto.etcContent || ''
          } else {
              // 나머지 A/B/C/D 항목
              form[key + 'Repoter'] = dto[key + 'Repoter']
              form[key + 'Content']  = dto[key + 'Content']
          }
        })
      } else {
        form.htmlContent = dto.htmlContent || ''
        // 에디터 모드라면 불러온 HTML 로 교체
        editor.commands.setContent(form.htmlContent || editorTableHtml)
      }
    }
  } catch(err) {
      console.error('RegistrationForm mounted 에러:', err)
  }
})


// idxDate 바뀔 때마다 해당 리포트 불러오기
watch(() => props.idxDate, async (newDate) => {
  if (!newDate || !form.deptCd) return

  console.log('날짜가 변경되었어요 -> ',props.idxDate)
  //템플릿 초기화
  items.forEach(({ key }) => {
  if (key === 'etc') form.etcContent = ''
  else {
    form[key+'Repoter'] = ''
    form[key+'Content'] = ''
  }
  })
  //에디터 초기화
  form.htmlContent = editorTableHtml

  try {
    const dto = await fetchReport(newDate, form.deptCd)
    form.contentMode = dto.contentMode

    if (form.contentMode === 'T') {
      // 템플릿 모드: 보고자 + 주요 내용 모두 복원
      items.forEach(({ key }) => {
        if (key === 'etc') {
          form.etcContent     = dto.etcContent  || ''
        } else {
          form[key+'Repoter'] = dto[key+'Repoter'] || ''
          form[key+'Content'] = dto[key+'Content']  || ''
        }
      })
    } else { // 에디터 모드: HTML 전체 복원
        form.htmlContent = dto.htmlContent || ''
        editor.commands.setContent(form.htmlContent || editorTableHtml)
    }
  } catch (err) {
    // 404면 '해당 날짜 데이터 없음' -> 폼 초기화
    if (err.response?.status === 404) {
      if (form.contentMode === 'T') {
        items.forEach(({ key }) => {
          if (key === 'etc') form.etcContent = ''
          else {
            form[key+'Repoter'] = ''
            form[key+'Content'] = ''
          }
        })
      } else {
        form.htmlContent = editorTableHtml
        editor.commands.setContent(form.htmlContent)
      }
    }
    else console.error('idxDate 변경 로드 실패', err)
  }
})


// 에디터 모드로 바뀔 때 저장된 HTML 있으면 그것을, 없으면 뼈대 테이블 가져옴
 watch(() => form.contentMode, mode => {
   if (mode === 'E') {
     const html = form.htmlContent || editorTableHtml
     editor.commands.setContent(html)
   }
 })


// 언마운트시 에디터 파기
onBeforeUnmount(() => {
  editor.destroy()
})


// (해당 부서의) 최근 보고서 데이터 가져오기
async function loadRecent() {
  const dto = await recentReport(form.deptCd)

  // 서버에서 온 모드로 템플릿/에디터 토글 동기화
  form.contentMode = dto.contentMode

  // 모드에 따라 각 필드 복원
  if (form.contentMode === 'T') {
    // 템플릿 모드: A/B/C/D + etc 복원
    items.forEach(({ key }) => {
      if (key === 'etc') {
        form.etcContent = dto.etcContent || ''
      } else {
        form[key + 'Repoter'] = dto[key + 'Repoter'] || ''
        form[key + 'Content']  = dto[key + 'Content']  || ''
      }
    })
  } else {
    // 에디터 모드: HTML 전체 복원
    form.htmlContent = dto.htmlContent || editorTableHtml

    // 에디터 인스턴스에도 반영
    if (editor) {
      editor.commands.setContent(form.htmlContent)
    }
  }
}

// 저장
async function submit() {
  // 간단 유효성: 날짜, 부서값 있는지
  if (!form.idxDate || !form.deptCd) return

 // 에디터 모드라면 inline min-width 제거
  if (form.contentMode === 'E') {
    form.htmlContent = stripMinWidth(form.htmlContent)
  }

  const payload = { ...form }  // contentMode와 해당 필드 모두 포함
  console.log("payload:", payload)

  try {
    // 수정이면 PUT, 아니면 POST
    const res = props.isEdit
        ? await updateReport(form.idxDate, form.deptCd, payload)
        : await createReport(payload);

        console.log("저장 res.data:", res.data)
        // 부모에게 DTO 넘겨주기
        emit('saved', res.data);
  } catch (error) {
    console.log("submit error:", error);
  }
}

</script>


<style scoped>
/* ========== 에디터 래퍼 스타일 ========= */
.editor {
  height: calc(100vh - 40px);
  width: 100%;
  box-sizing: border-box;
  overflow: auto;
}

/* ProseMirror 내부까지 높이·폭 상속 */
::v-deep(.editor .ProseMirror) {
  height: 100%;
  width: 100%;
  box-sizing: border-box;
  padding: 0;
}

/* ========== ReportView.vue 의 .navy-table 스타일을 에디터에도 그대로 적용 ========== */
/* (템플릿 모드 .navy-table 과 에디터 모드 table 모두 공통) */
.navy-table,
::v-deep(.editor .ProseMirror table),
::v-deep(.editor .navy-table) {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e0e0e0;
}

.navy-table th,
.navy-table td,
::v-deep(.editor .ProseMirror th),
::v-deep(.editor .ProseMirror td),
::v-deep(.editor .navy-table th),
::v-deep(.editor .navy-table td) {
  border: 1px solid #e0e0e0;
  padding: 12px;
  text-align: center;
  vertical-align: middle;
}

.navy-table th,
::v-deep(.editor .ProseMirror th),
::v-deep(.editor .navy-table th) {
  background-color: #002147;
  color: #ffffff;
}

.navy-table td {
  /* ReportView 에서는 td 아래쪽만 추가 보더 */
  border-bottom: 1px solid #e0e0e0;
}

.navy-table tbody tr:hover,
::v-deep(.editor .ProseMirror tbody tr:hover),
::v-deep(.editor .navy-table tbody tr:hover) {
  background-color: rgba(0, 33, 71, 0.05);
}

/* ========== 열 너비 고정: 구분 10%, 보고자 20%, 주요내용 70% ========== */
::v-deep(.editor .ProseMirror table) th:nth-child(1),
::v-deep(.editor .ProseMirror table) td:nth-child(1),
::v-deep(.navy-table) th:nth-child(1),
::v-deep(.navy-table) td:nth-child(1) {
  width: 15%;
}

::v-deep(.editor .ProseMirror table) th:nth-child(2),
::v-deep(.editor .ProseMirror table) td:nth-child(2),
::v-deep(.navy-table) th:nth-child(2),
::v-deep(.navy-table) td:nth-child(2) {
  width: 15%;
}

::v-deep(.editor .ProseMirror table) th:nth-child(3),
::v-deep(.editor .ProseMirror table) td:nth-child(3),
::v-deep(.navy-table) th:nth-child(3),
::v-deep(.navy-table) td:nth-child(3) {
  width: 70%;
}

/* 에디터 모드 주요내용(3열)만 왼쪽 정렬 */
/* .editor :deep(table th:nth-child(3)), */
.editor :deep(table td:nth-child(3)) {
  text-align: left;
}
/* ------------------ */

/* 에디터 안의 <table> 전체 폭 100% 강제 적용 */
::v-deep(.editor .ProseMirror table) {
  width: 100% !important;
}

/* 인라인 min-width 전부 제거 */
::v-deep(.editor .ProseMirror table),
::v-deep(.editor .ProseMirror table col),
::v-deep(.editor .ProseMirror table th),
::v-deep(.editor .ProseMirror table td) {
  min-width: unset !important;
}

.save-btn {
  margin-right: 80px;
}

/* ========== contenteditable 영역 스타일 ========== */
/* 보고자, 주요 내용, 비고 영역 모두 이 클래스를 사용 */
.editable-content {
  min-height: 48px;           /* 텍스트필드와 유사 높이(필요 시 조정) */
  padding: 6px 12px;          /* v-text-field/v-textarea의 padding 비슷하게 */
  border: 1px solid #e0e0e0;  /* 연한 회색 테두리 */
  border-radius: 4px;
  overflow-y: auto;
  line-height: 1.5;
  background-color: #fff;
  color: #000;
  /* 폰트, 크기 등은 필요에 따라 조정 가능 */
}
.editable-content:focus {
  outline: none;
  border-color: #1976d2; /* 포커스 시 Vuetify primary 색상 강조 */
}
/* 보고자 칸을 조금 더 컴팩트하게 보이고 싶으면 아래와 같이 높이 감축 */
.editable-content.reporter {
  min-height: 32px;
}
/* 주요 내용 칸(혹은 비고 칸)은 여러 줄 입력 가능하도록 높이 여유 둠 */
.editable-content.content-box {
  min-height: 80px;
}
</style>

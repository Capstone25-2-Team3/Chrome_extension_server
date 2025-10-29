# Chrome Extension Server

크롬 익스텐션에서 HTML을 파싱해 문장을 추출하고, FastAPI 서버를 통해 KoBERT 기반 혐오 표현 탐지 및 순화 처리를 수행합니다.

---

## 📁 파일 구조
.
├── backend/           # FastAPI 서버 및 모델 추론 로직
│   ├── main.py
│   ├── model.py
│   ├── utils.py
│   └── requirements.txt
│
├── extension/         # 크롬 익스텐션 코드
│   ├── content.js
│   ├── background.js
│   ├── popup.html
│   ├── style.css
│   └── manifest.json
│
├── data/              # 테스트용 문장, 전처리 텍스트
│
├── .gitignore
└── README.md

---

## 설치 및 실행 방법

### 1. 가상환경 생성 및 활성화

~~~bash
python3 -m venv venv
source venv/bin/activate
~~~

### 2. 패키지 설치
fastapi
uvicorn
pydantic
transformers
torch

### 3. 서버 실행
~~~bash
uvicorn backend.main:app --reload
~~~


# Chrome_extension_server
서버코드

## 파일 구조 
- backend/
FastAPI 서버 코드 및 KoBERT 모델 추론 로직

- extension/
크롬 익스텐션의 핵심: manifest + content.js + background.js

- data/
샘플 문장, 전처리된 텍스트, 테스트용 데이터셋

- .gitignore
__pycache__/, .env, *.pyc, node_modules/, venv/, *.ipynb_checkpoints 등 포함

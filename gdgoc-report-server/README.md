# GDGoC Report Server

댓글 혐오 표현 탐지 시스템의 **신고/피드백 수집 + DB 관리** 서버입니다.

## 기술 스택
- Java 17 + Spring Boot 3.3
- PostgreSQL + Flyway (DB 마이그레이션)
- Gradle (Groovy)

## 프로젝트 구조
```
src/main/java/com/gdgoc/report/
├── api/          # REST 컨트롤러
├── config/       # CORS, 예외처리 등
├── domain/       # 엔티티 + 리포지토리
│   ├── comment/
│   ├── inference/
│   ├── report/
│   ├── label/
│   └── cache/
├── dto/          # 요청/응답 DTO
└── service/      # 비즈니스 로직
```

## 로컬 개발

```bash
# 1. PostgreSQL에 DB 생성
createdb gdgoc_report

# 2. application-dev.yml에서 DB 접속 정보 확인

# 3. 실행
./gradlew bootRun

# 4. 확인
curl http://localhost:8080/health
```

## API 엔드포인트

| Method | Path | 설명 |
|--------|------|------|
| GET | `/health` | 헬스체크 |
| POST | `/api/comments` | 댓글 등록 |
| GET | `/api/comments/{id}` | 댓글 조회 |
| POST | `/api/reports` | 신고 접수 |
| GET | `/api/reports` | 신고 목록 (페이징) |
| GET | `/api/reports/comment/{id}` | 댓글별 신고 |
| GET | `/api/reports/stats` | 신고 통계 |
| POST | `/api/cache/lookup` | 순화 캐시 조회 |

## 서버 배포

```bash
# 빌드
./gradlew bootJar

# 서버로 전송
scp build/libs/gdgoc-report.jar ubuntu@서버IP:/home/ubuntu/gdgoc-report-server/
scp -r deploy/ ubuntu@서버IP:/home/ubuntu/gdgoc-report-server/

# 서버에서 (최초 1회)
cd /home/ubuntu/gdgoc-report-server
bash deploy/deploy.sh init

# .env 수정 후 배포
bash deploy/deploy.sh deploy

# 상태 확인
bash deploy/deploy.sh status

# 기존 FastAPI를 nohup → systemd 전환
bash deploy/deploy.sh migrate-fastapi
```

## 포트 구성
- `:8000` — FastAPI 추론 서버 (기존)
- `:8080` — Spring Boot 신고/피드백 서버 (신규)
- `:5432` — PostgreSQL

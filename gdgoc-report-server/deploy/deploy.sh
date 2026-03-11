#!/bin/bash
# ============================================================
# deploy.sh - 우분투 서버 초기 설정 + 배포 스크립트
#
# 사용법:
#   1) 로컬에서 빌드: ./gradlew bootJar
#   2) 서버로 전송:   scp build/libs/gdgoc-report.jar ubuntu@서버IP:/home/ubuntu/gdgoc-report-server/
#   3) 서버에서 실행:  bash deploy.sh [init|deploy|status]
# ============================================================

set -euo pipefail

APP_NAME="gdgoc-report"
APP_DIR="/home/ubuntu/gdgoc-report-server"
JAR_FILE="$APP_DIR/gdgoc-report.jar"
SERVICE_FILE="/etc/systemd/system/${APP_NAME}.service"

# 색상
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# ==================== 1. 초기 설정 (최초 1회) ====================
init() {
    log "=== 초기 환경 설정 시작 ==="

    # Java 17 설치
    if ! java -version 2>&1 | grep -q "17"; then
        log "Java 17 설치 중..."
        sudo apt update
        sudo apt install -y openjdk-17-jre-headless
    else
        log "Java 17 이미 설치됨"
    fi

    # PostgreSQL 설치
    if ! command -v psql &> /dev/null; then
        log "PostgreSQL 설치 중..."
        sudo apt install -y postgresql postgresql-contrib
        sudo systemctl enable postgresql
        sudo systemctl start postgresql
    else
        log "PostgreSQL 이미 설치됨"
    fi

    # DB 및 유저 생성
    log "데이터베이스 설정 중..."
    sudo -u postgres psql -tc "SELECT 1 FROM pg_roles WHERE rolname='gdgoc'" | grep -q 1 || \
        sudo -u postgres psql -c "CREATE USER gdgoc WITH PASSWORD 'change_me_please';"
    sudo -u postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='gdgoc_report'" | grep -q 1 || \
        sudo -u postgres psql -c "CREATE DATABASE gdgoc_report OWNER gdgoc;"

    warn "⚠️  DB 비밀번호를 변경하세요: sudo -u postgres psql -c \"ALTER USER gdgoc PASSWORD '새비밀번호';\""

    # 앱 디렉토리 생성
    mkdir -p "$APP_DIR/logs"

    # .env 파일 생성
    if [ ! -f "$APP_DIR/.env" ]; then
        cp "$(dirname "$0")/.env.example" "$APP_DIR/.env"
        warn "⚠️  $APP_DIR/.env 파일의 DB_PASS를 수정하세요"
    fi

    # systemd 서비스 등록
    log "systemd 서비스 등록 중..."
    sudo cp "$(dirname "$0")/gdgoc-report.service" "$SERVICE_FILE"
    sudo systemctl daemon-reload
    sudo systemctl enable "$APP_NAME"

    log "=== 초기 설정 완료 ==="
    log "다음 단계:"
    log "  1. $APP_DIR/.env 에서 DB_PASS 수정"
    log "  2. jar 파일을 $APP_DIR/ 에 복사"
    log "  3. bash deploy.sh deploy 실행"
}

# ==================== 2. 배포 (jar 교체 + 재시작) ====================
deploy() {
    log "=== 배포 시작 ==="

    if [ ! -f "$JAR_FILE" ]; then
        error "jar 파일이 없습니다: $JAR_FILE"
    fi

    # 기존 서비스 중지
    if systemctl is-active --quiet "$APP_NAME"; then
        log "기존 서비스 중지 중..."
        sudo systemctl stop "$APP_NAME"
        sleep 2
    fi

    # 서비스 시작
    log "서비스 시작 중..."
    sudo systemctl start "$APP_NAME"

    # 시작 확인 (최대 30초 대기)
    log "헬스체크 대기 중..."
    for i in $(seq 1 30); do
        if curl -sf http://localhost:8080/health > /dev/null 2>&1; then
            log "✅ 서버 정상 구동 확인! (${i}초)"
            return 0
        fi
        sleep 1
    done

    error "❌ 서버가 30초 내에 시작되지 않았습니다. 로그를 확인하세요: journalctl -u $APP_NAME -n 50"
}

# ==================== 3. 상태 확인 ====================
status() {
    echo ""
    log "=== 서비스 상태 ==="
    sudo systemctl status "$APP_NAME" --no-pager || true
    echo ""

    log "=== 최근 로그 (20줄) ==="
    journalctl -u "$APP_NAME" -n 20 --no-pager || true
    echo ""

    log "=== 포트 사용 현황 ==="
    ss -tlnp | grep -E ':(8080|8000|5432)' || echo "(사용 중인 포트 없음)"
}

# ==================== 4. FastAPI도 systemd로 전환 ====================
migrate_fastapi() {
    log "=== FastAPI nohup → systemd 전환 ==="

    # 기존 nohup 프로세스 종료
    warn "기존 nohup 으로 실행 중인 FastAPI 프로세스를 종료합니다..."
    pkill -f "uvicorn main:app" || true
    sleep 2

    # systemd 서비스 등록
    FASTAPI_SERVICE="/etc/systemd/system/gdgoc-fastapi.service"
    sudo cp "$(dirname "$0")/gdgoc-fastapi.service" "$FASTAPI_SERVICE"
    sudo systemctl daemon-reload
    sudo systemctl enable gdgoc-fastapi
    sudo systemctl start gdgoc-fastapi

    log "FastAPI 서비스 상태:"
    sudo systemctl status gdgoc-fastapi --no-pager || true
    log "=== 전환 완료 ==="
}

# ==================== 메인 ====================
case "${1:-help}" in
    init)           init ;;
    deploy)         deploy ;;
    status)         status ;;
    migrate-fastapi) migrate_fastapi ;;
    *)
        echo "사용법: $0 {init|deploy|status|migrate-fastapi}"
        echo ""
        echo "  init            - 최초 서버 환경 설정 (Java, PostgreSQL, systemd)"
        echo "  deploy          - jar 배포 + 서비스 재시작"
        echo "  status          - 서비스 상태 확인"
        echo "  migrate-fastapi - 기존 FastAPI를 nohup에서 systemd로 전환"
        ;;
esac

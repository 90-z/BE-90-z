#!/bin/bash

# 실행 중인 Spring Boot 애플리케이션 종료
echo "Stopping Spring Boot application..."

# 8080 포트를 사용하는 프로세스 찾기
PID=$(sudo lsof -t -i:8080)

if [ -z "$PID" ]; then
    echo "No application running on port 8080"
else
    echo "Killing process $PID"
    sudo kill -9 $PID
    sleep 5

    # 프로세스가 여전히 실행 중인지 확인
    if sudo kill -0 $PID 2>/dev/null; then
        echo "Force killing process $PID"
        sudo kill -9 $PID
    fi
    echo "Application stopped successfully"
fi

exit 0
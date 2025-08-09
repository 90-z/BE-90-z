#!/bin/bash

cd /home/ec2-user/app

echo "Starting Spring Boot application..."

# JAR 파일 찾기
JAR_FILE=$(find . -name "*.jar" | grep -v "plain" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: No JAR file found!"
    exit 1
fi

echo "Found JAR file: $JAR_FILE"

# 백그라운드에서 애플리케이션 실행
nohup java -jar $JAR_FILE > /home/ec2-user/app/application.log 2>&1 &

# 애플리케이션이 시작될 때까지 대기
echo "Waiting for application to start..."
for i in {1..30}; do
    if curl -f http://localhost:8080/actuator/health 2>/dev/null; then
        echo "Application started successfully!"
        exit 0
    fi
    echo "Waiting... ($i/30)"
    sleep 2
done

echo "Application may not have started properly. Check logs:"
tail -20 /home/ec2-user/app/application.log
exit 1
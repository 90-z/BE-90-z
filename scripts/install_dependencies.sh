#!/bin/bash

cd /home/ec2-user/app

echo "Installing dependencies and building application..."

# Java가 설치되어 있는지 확인
if ! command -v java &> /dev/null; then
    echo "Installing Java 17..."
    sudo yum update -y
    sudo yum install -y java-17-amazon-corretto-devel
fi

# 기존 빌드 파일 정리
echo "Cleaning previous builds..."
if [ -f "gradlew" ]; then
    chmod +x gradlew
    ./gradlew clean
elif [ -f "mvnw" ]; then
    chmod +x mvnw
    ./mvnw clean
fi

# 애플리케이션 빌드
echo "Building application..."
if [ -f "gradlew" ]; then
    ./gradlew build -x test
elif [ -f "mvnw" ]; then
    ./mvnw package -DskipTests
elif [ -f "build.gradle" ]; then
    gradle build -x test
elif [ -f "pom.xml" ]; then
    mvn package -DskipTests
fi

echo "Dependencies installation completed"
exit 0
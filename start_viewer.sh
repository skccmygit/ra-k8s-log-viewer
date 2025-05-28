#!/bin/bash

JAR_NAME="demo-0.0.1-SNAPSHOT.jar"

# 1. 실행 중인 프로세스 확인
PID=$(ps -ef | grep "$JAR_NAME" | grep -v grep | awk '{print $2}')

# 2. 프로세스가 존재하면 종료
if [ -n "$PID" ]; then
  echo "기존 프로세스($PID)를 종료합니다."
  kill -9 $PID
  sleep 1
fi

# 3. 다시 실행
echo "$JAR_NAME 실행을 시작합니다..."
nohup java -jar "$JAR_NAME" > app.log 2>&1 &

echo "실행 완료. 로그는 app.log 에 저장됩니다."
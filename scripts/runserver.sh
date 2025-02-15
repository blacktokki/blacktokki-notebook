#!/bin/sh

# Usage:
# bash scripts/runserver.sh

var=feedynote

# kill -9 `ps -ef | grep .vscode-server/extensions | grep redhat.java | awk '{print $2}'`
PID=`ps -ef | grep java | grep war | grep $var | awk '{print $2}'`
if [ -n "$PID" ]
then
  echo "=====$var is running at" $PID
else
  # ./gradlew build
  echo "=====$var isn't running====="
  nohup java -jar build/libs/$var*.war >/dev/null 2>&1 &
fi
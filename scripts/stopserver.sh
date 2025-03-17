#!/bin/sh

# Usage:
# bash scripts/runserver.sh ([param]...)
# bash ../scripts/runserver.sh ([param]...)
# * param: service name (vscode)

var=${1:-notebook}
if [ $var == "vscode" ]
then
  PID=`ps -ef | grep .vscode-server/extensions | grep redhat.java | awk '{print $2}'`
  echo $PID
else
  PID=`ps -ef | grep java | grep war | grep $var | awk '{print $2}'`
fi

if [ -n "$PID" ]
then
  echo "=====$var is running at" $PID
  kill -9 $PID
else
  echo "=====$var isn't running====="
fi
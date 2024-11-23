#!/usr/bin/env sh
if [ -n "$JAVA_OPTS" ]; then
  exec java $JAVA_OPTS -jar /api.jar
else
  exec java -jar /api.jar
fi
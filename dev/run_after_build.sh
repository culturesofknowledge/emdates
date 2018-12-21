#!/bin/bash
./gradlew clean build  
if [ $? != 0 ]; then
	exit 1;
fi

java -agentlib:jdwp=transport=dt_socket,server=y,address=5005,suspend=n -jar build/libs/lobsang-full.jar server config-template.yml

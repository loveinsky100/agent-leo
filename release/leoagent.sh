#!/bin/bash
if [[ $1 == 'start' ]]; then
	JAVA_VERSION=`java -version 2>&1 |awk 'NR==1{ gsub(/"/,""); print $3 }'`
	if [[ $JAVA_VERSION < '1.8' ]]; then
		echo "java version required 1.8, current version is $JAVA_VERSION";
		exit 1;
	fi

	AGENT_PATH="agent"
	if [ ! -e ${AGENT_PATH}/leo-agent.jar ]; then
		echo "Downloading leo-agent.jar"
		rm -rf $AGENT_PATH
		mkdir $AGENT_PATH
		cd $AGENT_PATH
		curl -o leo-agent.jar https://raw.githubusercontent.com/loveinsky100/agent-leo/master/release/leo-agent-1.0.0.jar
		cd ../
	else
		echo "Using ${AGENT_PATH}/leo-agent.jar"
	fi

	AGENT_PATH="agent"
	if [ ! -e ${AGENT_PATH}/config.json ]; then
		echo "Downloading default config"
		cd $AGENT_PATH
		curl -o config.json https://raw.githubusercontent.com/loveinsky100/agent-leo/master/release/config.json
		cd ../
	else
		echo "Using ${AGENT_PATH}/config.json"
	fi

	AGENT_PATH="agent"
	if [ ! -e log/main.log ]; then
		mkdir log
	fi

	nohup java -jar ${AGENT_PATH}/leo-agent.jar > log/main.log &

	echo -e "\n\n\
	      _                    _      	\n\
	     / \\   __ _  ___ _ __ | |_   	\n\
	    / _ \\ / _\` |/ _ \\ \`_ \\|  	\n\
	   / ___ \\ (_| |  __/ | | | |_   	\n\
	  /_/   \\_\\__, |\\___|_| |_|\\__\\   \n\
	          |___/                             \n\
	                                            \n\
	      AgentLeo Server 1.0.0 Start      	\n\
	========================================="

	sleep 3
	exit 0
fi

if [[ $1 == 'stop' ]]; then	 
	PROCESS=`ps -ef|grep leo-agent|grep -v grep|grep -v PPID|awk '{ print $2}'`
	for i in $PROCESS
	do
	  echo "Stop leo-agent"
	  kill -9 $i
	done
fi
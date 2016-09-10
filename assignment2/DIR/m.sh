#!/bin/bash
exec 2> /dev/null
javac ${1}
result=$?
foo=${1}
foo=${foo%$".java"}
if [ $result -eq 0 ] 
then
	{
	java $foo
	} &> /dev/null
	ret=$?
	if [ $ret -eq 0 ]
	then 
		echo "GOOD"
	else
		echo "CRASH"
	fi
else
	echo "BAD"
fi

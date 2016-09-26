#!/bin/bash
jarFile="$(find -name "*.jar")"
args1="$(find ${1} -name "INPUT.java")"
args2="$(find ${1} -name "m.sh")"
args3="${1}/MINI.java"
touch $args3
java -jar $jarFile $args1 $args2 $args3

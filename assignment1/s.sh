#!/bin/bash
dir="${1}"
javac -classpath $dir "${3}.java"
javac -classpath $dir "${4}.java"
p="$(find -name RTSmini.java)"
javac ${p}
class1="$(find ${1} -name "*.java")"
class2="$(find ${2} -name "*.java")"
javac ${class1}
javac ${class2}
foo=$(find -name RTSmini.class)
foo=${foo#$"./"}
foo=${foo%$".class"}
java $foo ${1} ${2} ${3} ${4}

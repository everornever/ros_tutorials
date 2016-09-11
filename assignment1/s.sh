#!/bin/bash
args=("$@")
for ((i=2;i<$#;i++));do
x[$(($i-2))]="$(find ${1} -name "${args[i]}.java")"
x[$(($i-2))]=${x[$(($i-2))]%$".java"}
done
echo ${x[1]}
echo ${x[@]}
p="$(find -name RTSmini.java)"
javac ${p}
class1="$(find ${1} -name "*.java")"
class2="$(find ${2} -name "*.java")"
javac ${class1}
javac ${class2}
foo=$(find -name RTSmini.class)
foo=${foo#$"./"}
foo=${foo%$".class"}
java $foo "${x[@]}"


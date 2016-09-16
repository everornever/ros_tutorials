#!/bin/bash
git clone ${1} "./javaDir"
path="$(find -name "javaDir")"
mkdir -p "dir"
p="$(find "./javaDir" -name "*.java")"
cp $p "dir" 2>/dev/null
jarFile="$(find -name "*.jar")"
echo $jarFile
java -jar $jarFile "dir"

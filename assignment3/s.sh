#!/bin/bash
git clone ${1} "./javaDir"
cd "./javaDir"
commit="$(git rev-list --branches -n 1 --first-parent --before=2016-09-13)"
echo $commit 
git reset --hard $commit
cd ".."
path="$(find -name "javaDir")"
mkdir -p "dir"
p="$(find "./javaDir" -name "*.java")"
cp $p "dir" 2>/dev/null
jarFile="$(find -name "MSRmini.jar")"
java -jar $jarFile "dir"

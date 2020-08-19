#!/bin/bash

ROOT=`pwd`

JAVA_EXEC=/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/bin/java 
echo JAVA=$JAVA_EXEC

EvosuiteTest="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar"

$EvosuiteTest evosuite.shell.excel.MergeExcels $ROOT 
$EvosuiteTest evosuite.shell.experiment.ExcelMethodCollector $ROOT

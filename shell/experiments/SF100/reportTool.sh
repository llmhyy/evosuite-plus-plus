#!/bin/bash

ROOT=`pwd`

JAVA_EXEC=/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/bin/java 
echo JAVA=$JAVA_EXEC

REPORT_TOOL="$JAVA_EXEC -jar $PWD/experiment-utils.jar"
REPORT_TOOL_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar $PWD/experiment-utils.jar"

NEW_REPORT=$ROOT/14Feb-fbranch.xlsx
OLD_REPORT=$ROOT/13Feb-branch.xlsx

$REPORT_TOOL -keys data#ProjectId#Class#Method -c -input_excels $OLD_REPORT $NEW_REPORT -workingFolder $ROOT/evoTest-reports -cmpRules "numIncr#Best Coverage" "numIncr#Avg Coverage" -cmpStats
$REPORT_TOOL -keys data#ProjectId#Class#Method -c -input_excels $OLD_REPORT $NEW_REPORT -workingFolder $ROOT/evoTest-reports -combCmpRules "numIncr#Best Coverage" "numIncr#Avg Coverage" "numDecr#Age of Best Coverage"

#!/bin/bash

ROOT=`pwd`

#JAVA_EXEC=/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/bin/java 
#echo JAVA=java
JAVA_EXEC=java

EVOTEST="$JAVA_EXEC -jar $PWD/evosuite-shell-1.0.7-SNAPSHOT.jar" 
#EVOTEST_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar $PWD/evosuite-shell-1.0.7-SNAPSHOT.jar"

EvosuiteTest="$JAVA_EXEC -cp $PWD/evosuite-shell-1.0.7-SNAPSHOT.jar"
TARGET_METHOD_FILE=$ROOT/graph-initial.txt
ITERATION=10
EXPERIMENT_BRANCH_FILE=$ROOT/summary.xlsx
FOLDER_NAME=graph-initial-mosa-localsearch-evoobj

rm $FOLDER_NAME/*
rm *.json

for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  rm -rf evosuite-tests
  rm -rf evosuite-reports
  rm -rf LOG*

  if [ !  -e $PROJECT.jar  ] ; then
   echo ERROR file $PROJECT.jar does not exist!!!
   exit 1
  fi
 
 TEST_OPTIONS="-inclusiveFile $TARGET_METHOD_FILE -testLevel lMethod -target $PROJECT.jar -iteration $ITERATION -reportFolder $FOLDER_NAME"
 OPTIONS="-Dsearch_budget 100 -Dinstrument_context true -Dp_test_delete 0 -Dp_test_change 0.9 -Dp_test_insert 0.3 -Dp_change_parameter 0.6 -Dp_functional_mocking 0 -Dmock_if_no_generator false -Dfunctional_mocking_percent 0 -Dprimitive_reuse_probability 0 -Dmin_initial_tests 10 -Dmax_initial_tests 20 -Ddse_probability 0 -Dinstrument_libraries true -Dinstrument_parent true -Dmax_attempts 100 -Dassertions false -Delite 10 -Ddynamic_pool 0.5 -Dprimitive_pool 0.5 -Djunit_check false -Dadopt_smart_mutation false -Dapply_object_rule true -Dlocal_search_rate 3"
 
 CMD_BRANCH="$EVOTEST -generateMOSuite -Dstrategy MOSUITE -Dalgorithm MOSA -criterion branch $TEST_OPTIONS $OPTIONS -exclFinishedMethods" 
 CMD_FBRANCH="$EVOTEST -generateMOSuite -Dstrategy MOSUITE -Dalgorithm MOSA -criterion fbranch $TEST_OPTIONS -branchExperimentFile $EXPERIMENT_BRANCH_FILE $OPTIONS -exclFinishedMethods"
 
 $CMD_BRANCH
 
 popd > /dev/null
done

#$EvosuiteTest evosuite.shell.excel.MergeExcels $ROOT 
#$EvosuiteTest evosuite.shell.experiment.ExcelMethodCollector $ROOT

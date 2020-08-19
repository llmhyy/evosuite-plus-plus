#!/bin/bash

ROOT=`pwd`

JAVA_EXEC=java 
echo JAVA=$JAVA_EXEC

EVOSUITE="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite"
EVOSUITE_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite"

EVOTEST="$JAVA_EXEC -jar $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar" 
EVOTEST_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar"

EvosuiteTest="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar"
TARGET_METHOD_FILE=$ROOT/experimentSets/targetMethod_byType_determined_set3.txt
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
  
 OPTIONS="-inclusiveFile $TARGET_METHOD_FILE -target $PROJECT.jar -iteration 3 -Dsearch_budget 100 -Dinstrument_context true -Dp_test_delete 0 -Dp_test_change 0.9 -Dp_test_insert 0.1 -Dp_change_parameter 0.1 -Dlocal_search_rate 30 -Dp_functional_mocking 0 -Dmock_if_no_generator false -Dfunctional_mocking_percent 0 -Dprimitive_reuse_probability 0 -Dmin_initial_tests 10 -Dmax_initial_tests 20 -Ddse_probability 0 -Dinstrument_libraries true -Dinstrument_parent true -Dmax_attempts 100 -Dassertions false -Delite 10 -Ddynamic_pool 0.0 -Dlocal_search_ensure_double_execution false"
 
 CMD_BRANCH="$EVOTEST -criterion branch -reportFolder report-branch $OPTIONS"
 CMD_FBRANCH="$EVOTEST -criterion fbranch -reportFolder report-fbranch $OPTIONS"
 
 #$CMD_BRANCH
 $CMD_FBRANCH 
 
 popd > /dev/null
done

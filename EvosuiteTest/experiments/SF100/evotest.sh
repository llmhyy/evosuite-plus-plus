#!/bin/bash

ROOT=`pwd`

JAVA_EXEC=/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/bin/java 
echo JAVA=$JAVA_EXEC

EVOSUITE="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite"
EVOSUITE_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite"

EVOTEST="$JAVA_EXEC -jar $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar" 
EVOTEST_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar"

EvosuiteTest="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar"
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
  
  #$EVOTEST -criterion fbranch -target $PROJECT.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 90 -inclusiveFile $ROOT/targetMethods.txt -Djunit_check false
  
  $EVOTEST -criterion fbranch -target $PROJECT.jar -generateRandom -generateSuite -Dsearch_budget 90 -inclusiveFile $ROOT/targetMethods.txt -Djunit_check false -Dinstrument_context true -Dp_test_delete 0 -Dp_test_change 0.9 -Dp_test_insert 0.1 -Dp_change_parameter 0.1 -Dp_functional_mocking 0 -Dmock_if_no_generator false -Dfunctional_mocking_percent 0 -Dprimitive_reuse_probability 0 -Dmin_initial_tests 5 -Dmax_initial_tests 30 -Ddse_probability 0 -Dinstrument_libraries true -Dinstrument_parent true -Dmax_length 1 -Dmax_size 1 -Dmax_attempts 100 -Dassertions false -Dstopping_condition maxgenerations -seed 100
 popd > /dev/null
done

$EvosuiteTest evosuite.shell.excel.MergeExcels $ROOT 
$EvosuiteTest evosuite.shell.experiment.ExcelMethodCollector $ROOT

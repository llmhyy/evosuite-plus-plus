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
  
  $EVOTEST -criterion branch -target $PROJECT.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 90 -inclusiveFile $ROOT/targetMethods.txt -Djunit_check false
  popd > /dev/null
done

$EvosuiteTest evosuite.shell.excel.MergeExcels $ROOT 
$EvosuiteTest evosuite.shell.experiment.ExcelMethodCollector $ROOT

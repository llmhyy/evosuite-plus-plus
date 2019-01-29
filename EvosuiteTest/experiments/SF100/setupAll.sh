#!/bin/bash

#----------------------------------------------------------
# define vars
#----------------------------------------------------------
DATA=`pwd`/classes.txt

JAVA_EXEC=/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/bin/java 
echo JAVA=$JAVA_EXEC

EVOSUITE="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite"
EVOSUITE_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite"

EVOTEST_SETUP="$JAVA_EXEC -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar com.test.experiment.EvoTestTool" 
EVOTEST_SETUP_DEBUG="$JAVA_EXEC -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar com.test.experiment.EvoTestTool"

#----------------------------------------------------------
# clean up
#----------------------------------------------------------
rm -rf evoTest-reports
rm -f $DATA

#----------------------------------------------------------
# setup 
#----------------------------------------------------------
for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  rm -rf evosuite-files
  rm -rf evosuite-tests
  rm -rf evosuite-report
  rm -rf LOG*
  
  if [ !  -e $PROJECT.jar  ] ; then
   echo ERROR file $PROJECT.jar does not exist!!!
   exit 1
  fi
  
  $EVOSUITE -inheritanceTree -setup  $PROJECT.jar lib/*
  popd > /dev/null
done

#----------------------------------------------------------
# correct evosuite.properties file
#----------------------------------------------------------
$EVOTEST_SETUP -refineProp $PWD

#----------------------------------------------------------
# listClasses
#----------------------------------------------------------
for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  fullName=`basename $I` 	

  for class in $($EVOSUITE -listClasses -target $PROJECT.jar); do
     echo -e "$fullName\t$class" >> $DATA
  done	

  popd > /dev/null
done
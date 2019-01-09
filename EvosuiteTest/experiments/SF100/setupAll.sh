#!/bin/bash

DATA=`pwd`/classes.txt

rm -f $DATA

for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  rm -rf evosuite-files
  rm -rf evosuite-tests
  rm -rf evosuite-reports
  rm -rf LOG*
  
  if [ !  -e $PROJECT.jar  ] ; then
   echo ERROR file $PROJECT.jar does not exist!!!
   exit 1
  fi
  
  EvoSuite -inheritanceTree -setup  $PROJECT.jar lib/*

  fullName=`basename $I` 	

  for class in $(EvoSuite -listClasses -target $PROJECT.jar); do
     echo -e "$fullName\t$class" >> $DATA
  done	

  popd > /dev/null
done

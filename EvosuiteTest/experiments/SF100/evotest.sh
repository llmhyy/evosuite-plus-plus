#!/bin/bash

ROOT=`pwd`
for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  # rm -rf evosuite-files
  rm -rf evosuite-tests
  rm -rf evosuite-reports
  rm -rf LOG*
  rm -rf allMethods.txt  
  rm -rf distribution.xlsx
  rm -rf progress.xlsx

  if [ !  -e $PROJECT.jar  ] ; then
   echo ERROR file $PROJECT.jar does not exist!!!
   exit 1
  fi
  
  $EVOTEST -criterion branch -target $PROJECT.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 90 -inclusiveFile $ROOT\targetMethods.txt -Djunit_check false
  popd > /dev/null
done

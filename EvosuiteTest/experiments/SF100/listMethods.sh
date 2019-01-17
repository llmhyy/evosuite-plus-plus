#!/bin/bash

rm evosuite-reports
for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  rm -rf evosuite-tests
  rm -rf evosuite-reports
  rm -rf targetMethods.txt 
  
  $EVOTEST -target $PROJECT.jar -listMethods

  popd > /dev/null
done

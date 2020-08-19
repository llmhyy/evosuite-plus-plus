#!/bin/bash

rm evosuite-reports
for I in *_*; do
  PROJECT=$(echo $I | awk 'BEGIN {FS="_"} ; { print $2}')
  echo $PROJECT
  pushd . > /dev/null
  cd $I
  
  $EVOTEST -target $PROJECT.jar -listMethods

  popd > /dev/null
done

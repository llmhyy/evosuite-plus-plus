#!/bin/bash

source common.sh

java -jar $EVOSUITE_SHELL -target $TARGET \
-projectCP $TARGET \
-listMethods 
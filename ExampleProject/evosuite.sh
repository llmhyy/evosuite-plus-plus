#!/bin/bash

source common.sh

java -jar $EVOSUITE_SHELL \
    -projectCP $TARGET \
    -generateSuite \
    -inclusiveFile target-methods.txt \
    -target $TARGET \
    -iteration 1 \
    -reportFolder reports \
    -seed 2022 \
    -Dapply_object_rule true \
    -Dsearch_budget 1 \
    -Dstrategy Evosuite \
    -Dalgorithm MONOTONIC_GA \
    -Dlog.level=INFO
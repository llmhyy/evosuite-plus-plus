@echo off

setlocal enabledelayedexpansion

SET ROOT=%cd%
SET EvosuiteTest="java -cp %ROOT%/EvosuiteTest-1.0.6-SNAPSHOT.jar"

%EvosuiteTest% evosuite.shell.excel.MergeExcels %ROOT% 
%EvosuiteTest% evosuite.shell.experiment.ExcelMethodCollector %ROOT% 

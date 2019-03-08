@echo off

setlocal enabledelayedexpansion

SET REPORT_TOOL="java -jar %cd%/experiment-utils.jar"
SET REPORT_TOOL_DEBUG="java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar %cd%/experiment-utils.jar"
SET EvosuiteTest="java -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar"
SET MERGE_EXCEL=%EvosuiteTest% evosuite.shell.excel.MergeExcels 

SET ROOT=%cd%

SET FBRANCH_REPORT=%cd%\report-fbranch\all-methods-fbranch.xlsx
SET BENCHMARK_REPORT=%cd%\report-branch\all-methods-branch.xlsx

%MERGE_EXCEL% -o  -workingDir %cd% -excelSuffix _evotest.xlsx
%MERGE_EXCEL% -o  -workingDir %cd% -excelSuffix _evotest.xlsx

%REPORT_TOOL% -keys data#ProjectId#Class#Method -c -input_excels %BENCHMARK_REPORT% %FBRANCH_REPORT% -workingFolder %ROOT%/evoTest-reports -cmpRules "numIncr#Best Coverage" "numIncr#Avg Coverage" -cmpStats
%REPORT_TOOL% -keys data#ProjectId#Class#Method -c -input_excels %BENCHMARK_REPORT% %FBRANCH_REPORT% -workingFolder %ROOT%/evoTest-reports -combCmpRules "numIncr#Best Coverage" "numIncr#Avg Coverage" "numDecr#Age of Best Coverage"

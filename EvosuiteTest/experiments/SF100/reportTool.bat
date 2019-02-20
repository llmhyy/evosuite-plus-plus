@echo off

setlocal enabledelayedexpansion

SET REPORT_TOOL="java -jar %cd%/experiment-utils.jar"
SET REPORT_TOOL_DEBUG="java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar %cd%/experiment-utils.jar"

SET ROOT=%cd%

SET NEW_REPORT=%ROOT%/evoTest-reports/allMethods-fbranch.xlsx
SET OLD_REPORT=%ROOT%/evoTest-reports/allMethods-branch.xlsx

%REPORT_TOOL% -keys data#ProjectId#Class#Method -c -input_excels %OLD_REPORT% %NEW_REPORT% -workingFolder %ROOT%/evoTest-reports -cmpRules "numIncr#Best Coverage" "numIncr#Avg Coverage" -cmpStats
%REPORT_TOOL% -keys data#ProjectId#Class#Method -c -input_excels %OLD_REPORT% %NEW_REPORT% -workingFolder %ROOT%/evoTest-reports -combCmpRules "numIncr#Best Coverage" "numIncr#Avg Coverage" "numDecr#Age of Best Coverage"

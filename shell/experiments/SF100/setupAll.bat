@echo off

setlocal enabledelayedexpansion

rem ----------------------------------------------------------
rem define vars
rem ----------------------------------------------------------
DATA=%cd%/classes.txt

SET EVOSUITE=java -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite
SET EVOSUITE_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar org.evosuite.EvoSuite

SET EVOTEST_SETUP=java -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar evosuite.shell.experiment.EvoTestTool 
SET EVOTEST_SETUP_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar evosuite.shell.experiment.EvoTestTool 
echo Evotest=%EVOTEST%

rem----------------------------------------------------------
rem clean up
rem----------------------------------------------------------
if exist evoTest-reports del /f /s /q evoTest-reports 1>nul
if exist evoTest-reports rmdir /s /q evoTest-reports
del $DATA

rem----------------------------------------------------------
rem setup 
rem----------------------------------------------------------
for /D %%I in (*_*) do (
	set prjDir=%%I%
	echo prjDir=!prjDir!
	set PROJECT=!prjDir:*_=!
	echo PROJECT=!PROJECT!
	pushd !prjDir!
	
	if NOT exist !PROJECT!.jar (
		echo ERROR file !PROJECT!.jar does not exist!!!
		exit 1
	)
	dir 
	
	if exist evosuite-files del /f /s /q evosuite-files 1>nul
	if exist evosuite-files rmdir /s /q evosuite-files
	if exist evosuite-tests del /f /s /q evosuite-tests 1>nul
	if exist evosuite-tests rmdir /s /q evosuite-tests
	if exist evosuite-report del /f /s /q evosuite-report 1>nul
	if exist evosuite-report rmdir /s /q evosuite-report
	if exist LOG* del /f /s /q LOG* 1>nul
	if exist LOG* rmdir /s /q LOG*
	if exist allMethods.txt del allMethods.txt  
	if exist distribution.xlsx del distribution.xlsx
	if exist progress.xlsx del progress.xlsx
	
	%EVOSUITE% -inheritanceTree -setup  !PROJECT!.jar lib/*
	popd
)

rem----------------------------------------------------------
rem correct evosuite.properties file
rem----------------------------------------------------------
%EVOTEST_SETUP% -refineProp %cd%

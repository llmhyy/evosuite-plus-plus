@echo off

setlocal enabledelayedexpansion

SET EVOTEST_SETUP=java -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar com.test.EvosuiteSetupTool 
SET EVOTEST_SETUP_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar com.test.EvosuiteSetupTool
echo EVOTEST_SETUP=%EVOTEST_SETUP%
for /D %%I in (10_*) do (
	set prjDir=%%I%
	echo prjDir=!prjDir!
	set PROJECT=!prjDir:*_=!
	echo PROJECT=!PROJECT!
	pushd !prjDir!
	
	dir 
	if exist evosuite-tests del /f /s /q evosuite-tests 1>nul
	if exist evosuite-tests rmdir /s /q evosuite-tests
	if exist evosuite-reports del /f /s /q evosuite-reports 1>nul
	if exist evosuite-reports rmdir /s /q evosuite-reports
	if exist LOG* del /f /s /q LOG* 1>nul
	if exist LOG* rmdir /s /q LOG*
	if exist allMethods.txt del allMethods.txt  
	if exist distribution.xlsx del distribution.xlsx
	if exist progress.xlsx del progress.xlsx
	
	%EVOTEST_SETUP% -refineProp !PROJECT!
	popd
)

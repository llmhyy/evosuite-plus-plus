@echo off

setlocal enabledelayedexpansion

SET EVOTEST=java -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar
SET EVOTEST_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar
SET ROOT=%cd%
echo Evotest=%EVOTEST%
if exist evosuite-reports del /f /s /q evosuite-reports 1>nul
for /D %%I in (*_*) do (
	set prjDir=%%I%
	echo prjDir=!prjDir!
	set PROJECT=!prjDir:*_=!
	echo PROJECT=!PROJECT!
	pushd !prjDir!
	dir 	
	
	rem Clean up
	if exist evosuite-tests del /f /s /q evosuite-tests 1>nul
	if exist evosuite-tests rmdir /s /q evosuite-tests
	if exist evosuite-reports del /f /s /q evosuite-reports 1>nul
	if exist evosuite-reports rmdir /s /q evosuite-reports
	if exist LOG* del /f /s /q LOG* 1>nul
	if exist LOG* rmdir /s /q LOG*
	if exist allMethods.txt del allMethods.txt  
	if exist distribution.xlsx del distribution.xlsx
	if exist progress.xlsx del progress.xlsx
	
	SET exeCmd=%EVOTEST% -criterion branch -target !PROJECT!.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 90 -inclusiveFile !ROOT!\targetMethods.txt -Djunit_check false
	rem SET exeCmd=%EVOTEST_DEBUG% -criterion branch -target !PROJECT!.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 10
	echo !exeCmd!
	
	!exeCmd!
	popd
)

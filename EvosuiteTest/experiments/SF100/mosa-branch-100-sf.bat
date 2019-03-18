@echo off

setlocal enabledelayedexpansion

SET EVOTEST=java -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar
SET EVOTEST_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar

SET EvosuiteTest="java -cp %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar"

SET ROOT=%cd%
echo Evotest=%EVOTEST%

SET TARGET_METHOD_FILE=!ROOT!/target-method-100-sf.txt

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
	
	SET OPTIONS=-inclusiveFile !TARGET_METHOD_FILE! -testLevel lMethod -target !PROJECT!.jar -iteration 3 -Dsearch_budget 100 -Dinstrument_context true -Dp_test_delete 0 -Dp_test_change 0.9 -Dp_test_insert 0.1 -Dp_change_parameter 0.1 -Dlocal_search_rate 3 -Dp_functional_mocking 0 -Dmock_if_no_generator false -Dfunctional_mocking_percent 0 -Dprimitive_reuse_probability 0 -Dmin_initial_tests 10 -Dmax_initial_tests 20 -Ddse_probability 0 -Dinstrument_libraries true -Dinstrument_parent true -Dmax_attempts 100 -Dassertions false -Delite 10 -Ddynamic_pool 0.0 -Dlocal_search_ensure_double_execution false
	
	SET exeCmd_BRANCH=%EVOTEST% -generateMOSuite -Dstrategy MOSUITE -Dalgorithm MOSA -criterion branch !OPTIONS!
	
	SET exeCmd_FBRANCH=%EVOTEST% -generateMOSuite -Dstrategy MOSUITE -Dalgorithm MOSA -criterion fbranch !OPTIONS!
	
	echo !exeCmd_BRANCH!
	
	!exeCmd_BRANCH!
	rem !exeCmd_FBRANCH!
	
	popd
)

%EvosuiteTest% evosuite.shell.excel.MergeExcels !ROOT!
%EvosuiteTest% evosuite.shell.experiment.ExcelMethodCollector !ROOT!

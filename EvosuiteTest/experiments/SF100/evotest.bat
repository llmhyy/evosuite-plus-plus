@echo off

setlocal enabledelayedexpansion

SET EVOTEST=java -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar
SET EVOTEST_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar

SET EvosuiteTest="java -cp $PWD/EvosuiteTest-1.0.6-SNAPSHOT.jar"

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
	
	rem SET exeCmd=%EVOTEST% -criterion branch -target !PROJECT!.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 90 -inclusiveFile !ROOT!\targetMethods.txt -Djunit_check false
	rem SET exeCmd=%EVOTEST_DEBUG% -criterion branch -target !PROJECT!.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore -Dsearch_budget 10
	SET exeCmd=%EVOTEST% -criterion fbranch -target !PROJECT!.jar -generateRandom -generateSuite -Dsearch_budget 90 -inclusiveFile !ROOT!/targetMethods.txt -Djunit_check false -Dinstrument_context true -Dp_test_delete 0 -Dp_test_change 0.9 -Dp_test_insert 0.1 -Dp_change_parameter 0.1 -Dp_functional_mocking 0 -Dmock_if_no_generator false -Dfunctional_mocking_percent 0 -Dprimitive_reuse_probability 0 -Dmin_initial_tests 5 -Dmax_initial_tests 30 -Ddse_probability 0 -Dinstrument_libraries true -Dinstrument_parent true -Dmax_length 1 -Dmax_size 1 -Dmax_attempts 100 -Dassertions false -Dstopping_condition maxgenerations -seed 100
	
	echo !exeCmd!
	
	!exeCmd!
	popd
)

$EvosuiteTest evosuite.shell.excel.MergeExcels !ROOT!
$EvosuiteTest evosuite.shell.experiment.ExcelMethodCollector !ROOT!

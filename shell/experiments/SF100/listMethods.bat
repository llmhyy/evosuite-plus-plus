@echo off

setlocal enabledelayedexpansion

SET EVOTEST=java -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar
SET EVOTEST_DEBUG=java -agentlib:jdwp=transport=dt_socket,server=y,address=9595 -jar %cd%\EvosuiteTest-1.0.6-SNAPSHOT.jar
echo Evotest=%EVOTEST%
if exist targetMethods.txt del targetMethods.txt
if exist targetMethods.log del targetMethods.log
for /D %%I in (*_*) do (
	set prjDir=%%I%
	echo prjDir=!prjDir!
	set PROJECT=!prjDir:*_=!
	echo PROJECT=!PROJECT!
	pushd !prjDir!
	dir 	
	
	rem Clean up
	if exist targetMethods.txt del targetMethods.txt
	
	SET exeCmd=%EVOTEST% -target !PROJECT!.jar -listMethods
	
	echo !exeCmd!
	
	!exeCmd!
	popd
)

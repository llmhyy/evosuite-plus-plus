---------------------------------------------------------------------------
1. Set up SF100 
---------------------------------------------------------------------------
+ Download SF100 here (http://www.evosuite.org/files/SF110-20130704.zip).
+ Extract zip file to folder SF100.	
+ Copy all scripts under EvosuiteTest/experiment/SF100 to folder SF100
+ Open cmd and cd to SF100.
+ In cmd, run 
	setupAll.bat (Windows) / setupAll.sh (Unix)
(this script will setup configuration for EvoSuite to run, as well as some fix on classpath problem of generated properties files)
---------------------------------------------------------------------------
2. To run EvosuiteForMethod for all 100 projects:
---------------------------------------------------------------------------
+ Run 
	evotest.bat (Windows) / evotest.sh (Linux)

cls && mvn clean source:jar install -Ppackage-jars -T 4 -Dmaven.test.skip=true
pause
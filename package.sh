#!/bin/bash
mvn clean -DskipTests=true source:jar install -Ppackage-jars -T 4
# Testing Evosuite++ with Memetic Algorithms
This repository is a forked version of https://github.com/llmhyy/evosuite-plus-plus

To replicate the results presented in our final report:
1. Download the SF100 Benchmark: https://www.evosuite.org/experimental-data/sf100/ and extract it to a directory called `SF100`
2. Copy the scripts in this repository under `evosutie-plus-plus/scripts` and place them in the `SF100` directory from step 1.
3. Run the scripts.

The demo presented in the final project presentation can be found in this repository under `evosuite-plus-plus/ExampleProject`.

Our unofficial developer docs can be viewed at: https://clincoln8.github.io/evosuite-plus-plus/developer-docs.html.

[![Build Status](https://travis-ci.org/EvoSuite/evosuite.svg?branch=master)](https://travis-ci.org/EvoSuite/evosuite)
[![CircleCI](https://circleci.com/gh/EvoSuite/evosuite.svg?style=svg&circle-token=f00c8d84b9dcf7dae4a82438441823f3be9df090)](https://circleci.com/gh/EvoSuite/evosuite)

# Evosuite++
This project is a forked version of Evosuite. The original address of evosuite is here: https://github.com/EvoSuite/evosuite. The goal of this project aims to improve Evosuite from the perspective of gradient recovering, a paramount assumption of the effectiveness of SBST (Search-based Software Testing). The traditional SBST approaches defines the measurement (e.g., branch distance) for evaluating how far a generated test is away from covering a branch. Such a measurement is considered as a loss function (or fitness function) to guide test generation to cover specific branches with various search algorithm e.g., GA (Genetic Algorithm), Hill Climbing, etc. However, more often than not, the landscape of the search space is not continuous, which makes SBST approaches degrade to random testing.

The project (Evosuite++) aims to pinpoint when the landscape is not conitinuous and propose various approaches to restore the effectiveness of SBST, e.g., recovering gradients for inteprocedural flag problem and constructing shortcut seeds to facilitate test generation.

In this project, we enhance Evosuite in terms of branch distance gradient recovering, object construction, smarter mutation, etc.
Here is the relevant publication:
- Yun Lin, Jun Sun, Gordon Fraser, Ziheng Xiu, Ting Liu, and Jin Song Dong. Recovering Fitness Gradients for Interprocedural Boolean Flags in Search-Based Testing (ISSTA 2020), 440--451. (Acknowledge*: Thanks Lyly Tran's contribution to set up our experiment despite her name is not list on the paper.)
- Yun Lin, You Sheng Ang, Jun Sun, Gordon Fraser, and Jin Song Dong. Graph-based Seed Object Synthesis for Search-Based Unit Testing (ESEC/FSE 2021).

You may refer to our website for more information on this project and how to run the experiment demonstrated in our paper: https://sites.google.com/view/evoipf/home and https://sites.google.com/view/evoobj/home

# Building EvoSuite

EvoSuite uses [Maven](https://maven.apache.org/).

First, ensure you have maven installed, to check, run

```mvn -v```

To build EvoSuite in Eclipse, make sure you have the [M2Eclipse](http://www.eclipse.org/m2e/) plugin installed, and import EvoSuite as Maven project. This will ensure that Eclipse correctly configure the Maven project.

# Building EvoSuite in Eclipse

In eclipse, we need to import Evosuite projects by **Import>>Maven>>Existing Maven Projects**. In general, we may import the following projects for compiling Evosuite:
* evosuite
* evosuite-client
* evosuite-master
* evosuite-runtime
* evosuite-shell

## JDK version
EvoSuite++ supports JDK1.8. We are working on the project to support the above version.

## The path of tools.jar
After importing all the above projects, we need to modify pom.xml in evosuite project as follows (here is an Eclipse bug, which makes the IDE fail to recognize correct Java home path even if we set the correct JDK path in Eclipse, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=432992):
We find ```<tools-default>``` and replace its ```<exists>``` and ```<toolsjar>``` element with the file location inside
 project.
For example:
```
<id>tools-default</id>
  <activation>
    <activeByDefault>true</activeByDefault>
    <file>
      <exists>C:\Program Files\Java\jdk1.8.0_261\libs\tools.jar</exists>
    </file>
    </activation>
    <properties>
       <toolsjar>C:\Program Files\Java\jdk1.8.0_261\libs\tools.jar</toolsjar>
    </properties>
```

A more systematic way to resolve the problem can be referred here: 
add the following configuration in your eclipse.ini file before `-vmargs` option.
```
-vm
$YOUR_JDK_PATH$/jre/bin/server/jvm.dll
```
Then, right click the project >> Maven >> Ipdate Project ...
By this means, the problem can be fixed.

## Compilation
To work on EvoSuite++ source code (forked from EvoSuite project by Prof. Gordon Fraser), we need to compile Evosuite to generate some source code. We suggest to remove the dependency of evosuite-master component on evosuite-client-test (i.e., the evosuite-client test-jar file) to compile the source code first. The maven command to compile EvoSuite++ is:
`
mvn clean source:jar install -Ppackage-jars -T 4
`


After compilation, the "evosuite-master" project may have some compilation errors. In this case, we may include the ```target/generated-sources/jaxb``` folder as build path. 

# FAQ

1. If you encounter **com.sun** dependency issue:

    > Please replace the corresponding tools.jar with the absolute path of the jdk tools.jar and the error will go
                                                      away. 

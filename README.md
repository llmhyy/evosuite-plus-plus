[![Build Status](https://travis-ci.org/EvoSuite/evosuite.svg?branch=master)](https://travis-ci.org/EvoSuite/evosuite)
[![CircleCI](https://circleci.com/gh/EvoSuite/evosuite.svg?style=svg&circle-token=f00c8d84b9dcf7dae4a82438441823f3be9df090)](https://circleci.com/gh/EvoSuite/evosuite)

# Evosuite++
This project is a forked version of Evosuite. The original address of evosuite is here: https://github.com/EvoSuite/evosuite

In this project, we enhance Evosuite in term of branch distance graident recovering, object construction, smarter mutation, etc.
Here is the relevant publication:
- Yun Lin, Jun Sun, Gordon Fraser, Ziheng Xiu, Ting Liu, and Jin Song Dong. Recovering Fitness Gradients for Interprocedural Boolean Flags in Search-Based Testing (ISSTA 2020), to appear.



# Building EvoSuite

EvoSuite uses [Maven](https://maven.apache.org/).

To build EvoSuite on the command line, install maven and then call

```mvn compile```

To create a binary distribution that includes all dependencies you can
use Maven as well:

```mvn package```

To build EvoSuite in Eclipse, make sure you have the [M2Eclipse](http://www.eclipse.org/m2e/) plugin installed, and import EvoSuite as Maven project. This will ensure that Eclipse uses Maven to build the project.

# Building EvoSuite in Eclipse

In eclipse, we need to import Evosuite projects by "Import>>Maven>>Existing Maven Projects". In general, we may import the following projects for compiling Evosuite:
1. evosuite
2. evosuite-client
3. evosuite-master
4. evosuite-runtime
5. generated 
6. shaded
7. standalone-runtime
8. EvsouiteTest

After importing all the above projects, we need to modify pom.xml in evosuite project as follows:
We find <id>tools-default</id> and replace its <exists> and <toolsjar> element into the file location inside project.
For example:
```
<id>tools-default</id>
  <activation>
    <activeByDefault>true</activeByDefault>
    <file>
      <exists>C:\Users\linyun\Documents\git_space\evosuite\libs\tools.jar</exists>
    </file>
    </activation>
    <properties>
       <toolsjar>C:\Users\linyun\Documents\git_space\evosuite\libs\tools.jar</toolsjar>
    </properties>
```

It is fine that the "generated" project has some compilation errors. Nevertheless, the "evsouite-master" project may have some compilation error. In this case, we may include the target/generated-sources/jaxb folder as build path. Thus, we can close "generated" project.




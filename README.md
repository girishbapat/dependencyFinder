# dependencyFinder
Project for xoriant's aws workflow to find dependencies of current table
<ul>
 
<li>How to setup?</li>
1. Get this repository.<br>
2. Make sure you have following installed:<br>
Latest version of JDK 1.8<br>
Maven <br>
<b>Make sure you add both add JDK_HOME/bin and MAVEN_HOME/bin in path</b><br><br>
<li>How to run?</li>
From root directory of project from command prompt exeute below command:<br>
<code>mvn clean install test surefire-report:report</code><br><br>
<li>If you are using eclipse:</li>
Import existing project in eclipse<br>
From Maven build, run configuration, execute the below command<br>
<code>clean install test surefire-report:report</code><br>
Open <code>sure-fire.html</code> file from <code>target -> site</code> folder in any browser.<br>

<li>What project contains?</li>
This project contains services for finding dependencies for current project.<br>
Testcases consuming the services with both positive and negative scenarios<br>


</ul> 
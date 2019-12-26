# dependencyFinder
Project for xoriant's aws workflow to find dependencies of current table
<ul>
 
<li>How to setup?</li>
1. Get this repository.<br>
2. Make sure you have following installed:<br>
Latest version of JDK 1.8<br>
Maven <br>
<b>Make sure you add both JDK_HOME/bin and MAVEN_HOME/bin in your path</b><br><br>
<li>How to run?</li>
From root directory of project from command prompt execute below command:<br>
<code>mvn clean install test surefire-report:report</code><br><br>
<li>If you are using eclipse:</li>
Import existing project in eclipse<br>
From Maven build, run configuration, execute the below command<br>
<code>clean install test surefire-report:report</code><br>
Open <code>sure-fire.html</code> file from <code>target -> site</code> folder in any browser.<br>
<br>
<li>What project contains?</li>
This project contains services for finding dependent tables for current table.<br>
Testcases consuming the services with both positive and negative scenarios<br>
<br><br>
<strong>Note: I am using lombok library for auto-generating getters and setters etc.<br>You may get compilation errors.<br> To work on the same, either you include <a href="https://projectlombok.org/">Project Lombok</a> or generate getter setters</strong><br>
</ul> 
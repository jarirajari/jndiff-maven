The original project:

https://sourceforge.net/p/jndiff/code/HEAD/tree/trunk

License:

https://sourceforge.net/p/jndiff/code/HEAD/tree/trunk/lgpl.txt

changes in this project:
- ant to maven
- java 8
- remove GUI and use terminal
- executable jar
- improved API for other libraries and terminal usage
- removed tests
- relocated properties
- removed a lot of stuff that can be found from original project

if needed, install 3rd party dependencies with plugin:

mvn install:install-file -Dfile=./lib/ndiff.jar -DgroupId=it.unibo.cs -DartifactId=ndiff -Dversion=1.3.0-SNAPSHOT -Dpackaging=jar

Notes:
- check build.xml
- main class 'it.unibo.cs.ndiff.ui.Main'
- basic usage ' java -jar jndiff.jar '
- for example, "java -jar jndiff.jar -d doc1.xml doc2.xml delta.xml
- to pretty print the delta, "cat delta.xml | xmllint --format -"


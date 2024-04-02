

export JAVA_HOME="C:/Program Files/Java/jdk-21"

mvn clean install assembly:single
cp target/ws-jersey3-0.1-jar-with-dependencies.jar tmp/

cp pom.xml pom.xml.ori
cp pom.war.xml pom.xml

mv src/main/java/edu/doc_ti/bigdatamicroservices/jersey3/MainApp.java src/main/java/edu/doc_ti/bigdatamicroservices/jersey3/MainApp.java.xxx

mvn clean package
cp target/ws-jersey3.war tmp/ws-jersey3.war

cp pom.xml.ori pom.xml
mv src/main/java/edu/doc_ti/bigdatamicroservices/jersey3/MainApp.java.xxx src/main/java/edu/doc_ti/bigdatamicroservices/jersey3/MainApp.java


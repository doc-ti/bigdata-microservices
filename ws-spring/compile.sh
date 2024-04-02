

export JAVA_HOME="C:/Program Files/Java/jdk-21"

mvn clean install
cp target/ws-spring-0.1.jar tmp/

cp pom.xml pom.xml.ori
sed -i -e "s#<packaging>jar</packaging>#<packaging>war</packaging>#" pom.xml

mvn clean install
cp target/ws-spring-0.1.war tmp/

cp pom.xml.ori pom.xml


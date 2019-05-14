git pull origin master
mvn clean install docker:build -DskipTests -DskipDockerPush

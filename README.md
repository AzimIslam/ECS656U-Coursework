# ECS656U Coursework
Based upon https://github.com/sajeerzeji/SpringBoot-GRPC
Commands for preparing the enviornment (Assuming you are in the main folder e.g. the one with the pom.xml file in it)

Student Name: Azim Islam
Student Number: 190227344

## Initial setup
1. sudo apt update
2. sudp apt install default-jdk maven

## GRPC Server Setup
1. (From grpc-server folder) mvn package -Dmaven.test.skip=true
2. (From grpc-server folder) chmod 777 mvnw
3. (From grpc-server folder) ./mvnw spring-boot:run -Dmaven.test.skip=true

## GRPC Client Setup
1. (From grpc-client folder e.g. seperate ssh connection) mvn package -Dmaven.test.skip=true
2. (From grpc-client folder e.g. seperate ssh connection) chmod 777 mvnw
3. (From grpc-client folder e.g. seperate ssh connection) ./mvnw spring-boot:run -Dmaven.test.skip=true

# Generating a matrix using the script
```console
python generateSquareMatrix.py <dimensions> <range> > <output_file>
```
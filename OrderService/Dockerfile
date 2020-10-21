FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.8_10_openj9-0.21.0-alpine-slim

# Fabric8io
RUN wget -O /opt/run-java.sh https://raw.githubusercontent.com/fabric8io-images/run-java-sh/v1.3.8/fish-pepper/run-java-sh/fp-files/run-java.sh

EXPOSE 8080
EXPOSE 8081
EXPOSE 5005

# Add the service
ARG JAR_FILE
ADD ${JAR_FILE} /usr/share/vistair/app.jar

# Start using fabric8io
CMD JAVA_APP_DIR=/usr/share/vistair sh /opt/run-java.sh

# run this in the service directory
# docker build -t loyds --build-arg "JAR_FILE=./target/OrderService-0.0.1-SNAPSHOT.jar" .
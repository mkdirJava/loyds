#!/bin/sh
docker build -t loyds --build-arg "JAR_FILE=./target/OrderService-0.0.1-SNAPSHOT.jar" .


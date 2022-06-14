#!/bin/bash
set -o xtrace
./mvnw clean package -Dquarkus.kubernetes.deploy=true

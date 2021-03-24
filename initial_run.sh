#!/usr/bin/env bash
mvn install:install-file -Dfile=lib/RDFtoPGConverter.jar -DgroupId=org.example -DartifactId=rdf4j-getting-started -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar

#! /bin/bash

# Run Concordion on the Scaliger test suite from outside build environment
# Assumes './gradlew build' has succeeded.
# NOTE: Failures are common so long as inferred dates are present in the test
# where scholar used more context to infer such a date, than is present in
# the actual date as written.

# Exit on errors
set -e

# Debug this script
#set -x

# This is where Concordion output will appear
OUTPUT_DIR=/tmp/concordion

# Location where concordion-2.2.0.zip was unzipped
CONCORDION_DIR=~/src/concordion-2.2.0
CONCORDION_JAR=$CONCORDION_DIR/concordion-2.2.0.jar
CONCORDION_DEP=$CONCORDION_DIR/lib/*

# Run ./gradlew build, then this is where jar, test classes and resources are
EMDATES_JAR=./build/libs/lobsang-full.jar
CLASSES=./build/classes/java/test
RESOURCES=./build/resources/test

# Prepare to have JUnit run Concordion test case
JUNIT=org.junit.runner.JUnitCore
TEST=nl.knaw.huygens.lobsang.concordion.Scaliger
CLASSPATH=$CONCORDION_JAR:$CONCORDION_DEP:$EMDATES_JAR:$CLASSES:$RESOURCES

# Run Concordion, display only report count and filename of the report
java -Dconcordion.output.dir=$OUTPUT_DIR -cp $CLASSPATH $JUNIT $TEST 2>&1 \
	| egrep file://$OUTPUT_DIR\|Successes: \
	| grep -v '#' \
	| head -2

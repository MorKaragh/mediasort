#!/bin/bash
#mvn clean install
scp target/photohost-0.0.1-SNAPSHOT.jar mediasort:/opt/instaloader/mediasort.jar
ssh mediasort "service mediasort restart"
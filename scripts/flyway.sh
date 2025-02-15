#!/bin/sh

# Usage:
# bash scripts/flyway.sh param1
# * param1: flyway command (Migrate|Clean|...)

PROPERTY_FILE=./src/main/resources/datasource.yaml

function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat $PROPERTY_FILE | grep "$PROP_KEY" | cut -d':' -f2-`
   echo $PROP_VALUE
}

USER=$(getProperty "spring.datasource.user")
PASSWORD=$(getProperty "spring.datasource.password")
URL=$(getProperty "spring.datasource.url")

rm -rf ./build/resources/main/db
cp -r ./src/main/resources/db ./build/resources/main
./gradlew flyway$1 "-i" "-Dflyway.user=$USER" "-Dflyway.password=$PASSWORD" "-Dflyway.url=$URL" "-Dflyway.schemas=db1_feedynote"
# read -p "Press enter to continue"
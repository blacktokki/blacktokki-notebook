#!/bin/sh

# Usage:
# bash scripts/setup.sh param1 param2
# * param1: database username
# * param2: database password, secret key

DATASOURCE_FILE=./src/main/resources/datasource.yaml
SECRET_FILE=./src/main/resources/secret.yaml

# sudo apt-get update
# sudo apt-get --assume-yes install mysql-server-5.7
sudo mysql -u root -p" " -e"
create user '$1'@'%' identified by '$2';
GRANT ALL PRIVILEGES ON *.* TO '$1'@'%';
flush privileges;
SET character_set_server = 'utf8mb4';
create database db1_notebook;
"

sudo apt-get --assume-yes install openjdk-17-jdk
echo "spring.datasource.url: jdbc:mysql://127.0.0.1:3306/?rewriteBatchedStatements=true&characterEncoding=UTF-8" > $DATASOURCE_FILE  # &profileSQL=true&logger=Slf4JLogger&maxQuerySizeToLog=999999
echo "spring.datasource.username: $1" >> $DATASOURCE_FILE
echo "spring.datasource.password: $2" >> $DATASOURCE_FILE
echo "jwt.secret: $2" >> $SECRET_FILE
echo "jwt.pat-secret: $2" >> $SECRET_FILE

read -p "Press enter to continue"
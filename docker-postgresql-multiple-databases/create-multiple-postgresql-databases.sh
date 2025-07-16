#!/bin/bash

set -e
set -u

num=0

function create_user_and_database() {
	local database=$1
	# echo "  Creating user and database '$database'"
# 	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
# 	    CREATE USER $database;
# 	    CREATE DATABASE $database;
# 	    GRANT ALL PRIVILEGES ON DATABASE $database TO $database;
# EOSQL
	if [ "$( psql  -U root -d postgres -XtAc "SELECT 1 FROM pg_database WHERE datname='$database'" )" = '1' ]
	then
		echo "Database: $database already exists!"
	else
		echo "Database: $database does not exist, creating..."
		createdb $database
	fi
	# createdb $database 2> /dev/null || echo "database already exists!"
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
		create_user_and_database $db
	done
	echo "Multiple databases created"
fi
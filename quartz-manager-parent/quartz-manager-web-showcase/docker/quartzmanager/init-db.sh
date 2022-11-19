#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER quartzmanager PASSWORD 'quartzmanager';
	CREATE DATABASE "quartzmanager";
	GRANT ALL PRIVILEGES ON DATABASE "quartzmanager" TO quartzmanager;
	ALTER ROLE quartzmanager SUPERUSER;
EOSQL

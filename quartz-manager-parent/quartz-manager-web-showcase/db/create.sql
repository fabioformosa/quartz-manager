CREATE DATABASE quartzmanager;
CREATE USER quartzmanager PASSWORD 'quartzmanager';
GRANT ALL PRIVILEGES ON DATABASE quartzmanager TO quartzmanager;
ALTER ROLE quartzmanager SUPERUSER;

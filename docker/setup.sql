CREATE USER accounts WITH PASSWORD 'accounts';
CREATE DATABASE accounts WITH OWNER accounts ENCODING 'UTF8';

\c accounts

GRANT ALL PRIVILEGES ON DATABASE accounts TO accounts;

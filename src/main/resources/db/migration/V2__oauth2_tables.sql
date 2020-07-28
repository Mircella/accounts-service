DROP TABLE IF EXISTS oauth_client_details CASCADE;
DROP TABLE IF EXISTS oauth_code CASCADE;
DROP TABLE IF EXISTS oauth_access_token CASCADE;
DROP TABLE IF EXISTS oauth_refresh_token CASCADE;
DROP TABLE IF EXISTS oauth_client_token CASCADE;

CREATE TABLE oauth_client_details
(
    client_id character varying(255) NOT NULL,
    resource_ids character varying(255),
    client_secret character varying(255),
    scope character varying(255),
    authorized_grant_types character varying(255),
    web_server_redirect_uri text,
    authorities character varying(255),
    access_token_validity integer,
    refresh_token_validity integer,
    additional_information character varying(255),
    autoapprove character varying(255)
);

ALTER TABLE oauth_client_details
ADD CONSTRAINT pk_oauth_client_details PRIMARY KEY (client_id);

CREATE TABLE oauth_code (
    code character varying(255),
    authentication bytea
);

CREATE TABLE oauth_access_token
(
    token_id VARCHAR(255),
    token bytea,
    authentication_id VARCHAR(255) PRIMARY KEY,
    user_name VARCHAR(255),
    client_id VARCHAR(255),
    authentication bytea,
    refresh_token VARCHAR(255)
);

CREATE TABLE oauth_refresh_token
(
    token_id VARCHAR(255),
    token bytea,
    authentication bytea
);

CREATE TABLE oauth_client_token
(
    token_id VARCHAR(4000),
    token bytea,
    authentication_id VARCHAR(255) PRIMARY KEY,
    user_name VARCHAR(255),
    client_id VARCHAR(255)
);

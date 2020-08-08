INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
('test', 'test', 'read,write', 'password,implicit,authorization_code,refresh_token,client_credentials', 'http://localhost:8776/login', null, 36000, 36000, null, true);

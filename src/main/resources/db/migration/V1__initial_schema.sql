CREATE TABLE accounts (
    id uuid PRIMARY KEY,
    login varchar not null UNIQUE,
    password varchar not null
);

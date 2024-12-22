DROP TABLE if EXISTS userprofile;

CREATE TABLE IF NOT EXISTS userprofile (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    avatar BYTEA
);
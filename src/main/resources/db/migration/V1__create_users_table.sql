CREATE TABLE users
(
   id serial PRIMARY KEY,
   email VARCHAR (128) UNIQUE NOT NULL,
   password VARCHAR (255) NOT NULL,
   first_name VARCHAR (128) NOT NULL,
   last_name VARCHAR (128) NOT NULL,
   created_on TIMESTAMP NOT NULL,
   last_login TIMESTAMP
);
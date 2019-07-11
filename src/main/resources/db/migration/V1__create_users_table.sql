CREATE TABLE users (
   id serial PRIMARY KEY,
   email VARCHAR (355) UNIQUE NOT NULL,
   password VARCHAR (50) NOT NULL,
   first_name VARCHAR (355) NOT NULL,
   last_name VARCHAR (355) NOT NULL,
   created_on TIMESTAMP NOT NULL,
   last_login TIMESTAMP
);
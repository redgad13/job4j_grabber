CREATE TABLE IF NOT EXISTS post (
id serial PRIMARY KEY,
name varchar(50),
text text,
link text,
created timestamp
);
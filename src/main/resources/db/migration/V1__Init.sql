CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS users (
  id          SERIAL PRIMARY KEY,
  username    CITEXT COLLATE "C" NOT NULL UNIQUE,
  email       CITEXT COLLATE "C" NULL UNIQUE,
  rank        INTEGER DEFAULT 0,
  avatar_link TEXT,
  password    TEXT               NOT NULL
);

CREATE INDEX email_idx
  ON users (email);
CREATE INDEX username_idx
  ON users (username);

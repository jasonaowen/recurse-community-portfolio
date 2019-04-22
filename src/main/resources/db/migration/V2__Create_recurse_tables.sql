CREATE TABLE batches (
  batch_id INTEGER PRIMARY KEY,
  name TEXT UNIQUE NOT NULL,
  name_alt TEXT,
  name_short TEXT
);

CREATE TABLE recurse_profiles (
  recurse_profile_id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  github TEXT,
  twitter TEXT,
  image_url TEXT NOT NULL,
  directory_url TEXT NOT NULL
);

CREATE TABLE stints (
  recurse_profile_id INTEGER NOT NULL
    REFERENCES recurse_profiles(recurse_profile_id),
  batch_id INTEGER
    REFERENCES batches(batch_id),
  start_date DATE NOT NULL,
  end_date DATE,
  for_half_batch BOOLEAN NOT NULL,
  title TEXT,
  stint_type TEXT NOT NULL,
  PRIMARY KEY (recurse_profile_id, start_date)
);

ALTER TABLE users
ADD FOREIGN KEY (recurse_profile_id)
REFERENCES recurse_profiles(recurse_profile_id);

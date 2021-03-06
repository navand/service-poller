CREATE TABLE IF NOT EXISTS users
(
  id       SERIAL NOT NULL,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
  );

CREATE TABLE IF NOT EXISTS services
(
  id      SERIAL NOT NULL,
  user_id BIGINT NOT NULL,
  name    VARCHAR(255) NOT NULL,
  url     VARCHAR(255) NOT NULL,
  status  VARCHAR(10),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
  );

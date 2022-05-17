DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;

create TABLE IF NOT EXISTS user
(
    id        INT AUTO_INCREMENT,
    username  VARCHAR(200) UNIQUE NOT NULL,
    password  VARCHAR(256)        NOT NULL,
    deposit  INT                  NOT NULL COMMENT 'User deposit',
    role VARCHAR(50),
    PRIMARY KEY (id)
) ;

CREATE TABLE IF NOT EXISTS role
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_role
(
    id           INT PRIMARY KEY AUTO_INCREMENT COMMENT 'user role id',
    user_id      BIGINT COMMENT 'User id',
    role_id VARCHAR(50) COMMENT 'Role id'
);

INSERT INTO role (id, name)
VALUES (1, 'ROLE_BUYER'),
       (2, 'ROLE_SELLER');
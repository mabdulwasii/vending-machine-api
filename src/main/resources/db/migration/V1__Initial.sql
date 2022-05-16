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

INSERT INTO user (id, username, password, deposit)
VALUES (1, 'admin@example.com', '$2a$04$Ot6tX0QK8xzo/xW5A/J3F.QZDS7eio095dN5IoQjWJDOySs42f1S.', 0),
       (2, 'user@example.com', '$2a$04$Ot6tX0QK8xzo/xW5A/J3F.QZDS7eio095dN5IoQjWJDOySs42f1S.', 0);

INSERT INTO role (id, name)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2);

CREATE TABLE IF NOT EXISTS user_tbl (
    id VARCHAR(10) NOT NULL,
    address VARCHAR(500) NULL,
    avatar MEDIUMTEXT NULL,
    email VARCHAR(320) NULL,
    nickname VARCHAR(10) NULL,
    password VARCHAR(500) NOT NULL,
    phone VARCHAR(15) NULL,
    PRIMARY KEY (id)
);

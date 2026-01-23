CREATE TABLE users (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        username VARCHAR(120) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        roles VARCHAR(200) NOT NULL DEFAULT 'USER',
                        enabled BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_users_username (username)
);

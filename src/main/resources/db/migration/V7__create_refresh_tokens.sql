CREATE TABLE refresh_tokens (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                token VARCHAR(500) NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                revoked_at TIMESTAMP NULL,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_refresh_tokens_token (token),
                                KEY idx_refresh_tokens_user (user_id),
                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id)
                                        ON DELETE CASCADE
);

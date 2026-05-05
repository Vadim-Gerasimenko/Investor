CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.users
(
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    registered_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at    TIMESTAMP,
    last_activity_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auth_users_email ON auth.users (email);
CREATE INDEX IF NOT EXISTS idx_auth_users_registered_at ON auth.users (registered_at);

COMMENT ON TABLE auth.users IS 'Пользователи системы';
COMMENT ON COLUMN auth.users.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN auth.users.email IS 'Email пользователя';
COMMENT ON COLUMN auth.users.password IS 'Хеш пароля';
COMMENT ON COLUMN auth.users.registered_at IS 'Дата и время регистрации';
COMMENT ON COLUMN auth.users.last_login_at IS 'Дата и время последнего входа';
COMMENT ON COLUMN auth.users.last_activity_at IS 'Дата и время последнего действия';


CREATE TABLE IF NOT EXISTS auth.roles
(
    name VARCHAR(50) PRIMARY KEY
);

COMMENT ON TABLE auth.roles IS 'Роли в системе';
COMMENT ON COLUMN auth.roles.name IS 'Название роли';

INSERT INTO auth.roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN'),
       ('ROLE_MODERATOR'),
       ('ROLE_SYSTEM');


CREATE TABLE IF NOT EXISTS auth.user_roles
(
    user_id BIGINT      NOT NULL,
    role_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES auth.roles (name)
);

COMMENT ON TABLE auth.user_roles IS 'Роли пользователей в системе';
COMMENT ON COLUMN auth.user_roles.user_id IS 'ID пользователя';
COMMENT ON COLUMN auth.user_roles.role_id IS 'Название роли';


CREATE TABLE IF NOT EXISTS auth.tokens
(
    id            BIGSERIAL PRIMARY KEY,
    access_token  VARCHAR(255) NOT NULL UNIQUE,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    is_logged_out BOOLEAN      NOT NULL DEFAULT FALSE,
    user_id       BIGINT       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_auth_tokens_user_id ON auth.tokens (user_id);

COMMENT ON TABLE auth.tokens IS 'JWT токены аутентификации';
COMMENT ON COLUMN auth.tokens.access_token IS 'Access токен';
COMMENT ON COLUMN auth.tokens.refresh_token IS 'Refresh токен';
COMMENT ON COLUMN auth.tokens.is_logged_out IS 'Вышел ли пользователь из системы';
COMMENT ON COLUMN auth.tokens.user_id IS 'ID пользователя';


CREATE TABLE IF NOT EXISTS auth.user_profiles
(
    user_id      BIGINT      NOT NULL PRIMARY KEY,
    first_name   VARCHAR(50) NOT NULL,
    last_name    VARCHAR(50) NOT NULL,
    middle_name  VARCHAR(50),
    birth_date   DATE        NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    avatar_url   VARCHAR(500),
    updated_at   DATE,
    FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_auth_user_profiles_phone ON auth.user_profiles (phone_number);

COMMENT ON TABLE auth.user_profiles IS 'Профили пользователей';
COMMENT ON COLUMN auth.user_profiles.user_id IS 'ID пользователя';
COMMENT ON COLUMN auth.user_profiles.first_name IS 'Имя';
COMMENT ON COLUMN auth.user_profiles.middle_name IS 'Отчество';
COMMENT ON COLUMN auth.user_profiles.last_name IS 'Фамилия';
COMMENT ON COLUMN auth.user_profiles.birth_date IS 'Дата рождения';
COMMENT ON COLUMN auth.user_profiles.phone_number IS 'Номер телефона';
COMMENT ON COLUMN auth.user_profiles.avatar_url IS 'URL аватара';
COMMENT ON COLUMN auth.user_profiles.updated_at IS 'Дата и время обновления профиля';
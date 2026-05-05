CREATE SCHEMA IF NOT EXISTS tbank;
CREATE SCHEMA IF NOT EXISTS dictionary;

CREATE TABLE IF NOT EXISTS tbank.tokens
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(500) NOT NULL UNIQUE,
    token_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tbank_tokens_user FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tbank_tokens_user_id ON tbank.tokens (user_id);

COMMENT ON TABLE tbank.tokens IS 'Токены доступа к Т-Инвестициям API';
COMMENT ON COLUMN tbank.tokens.id IS 'ID записи';
COMMENT ON COLUMN tbank.tokens.user_id IS 'ID пользователя - владельца токена';
COMMENT ON COLUMN tbank.tokens.token IS 'Секретный токен для доступа к API';
COMMENT ON COLUMN tbank.tokens.token_name IS 'Название токена';
COMMENT ON COLUMN tbank.tokens.created_at IS 'Дата и время создания токена';

CREATE TABLE IF NOT EXISTS tbank.active_tokens
(
    user_id  BIGINT PRIMARY KEY,
    token_id BIGINT UNIQUE NOT NULL,
    CONSTRAINT fk_tbank_active_tokens_user FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_tbank_active_tokens_token FOREIGN KEY (token_id) REFERENCES tbank.tokens (id) ON DELETE CASCADE
);

COMMENT ON TABLE tbank.active_tokens IS 'Токены доступа к Т-Инвестициям API, используемые в текущий момент';
COMMENT ON COLUMN tbank.active_tokens.user_id IS 'ID пользователя - владельца токена';
COMMENT ON COLUMN tbank.active_tokens.token_id IS 'ID токена, используемого в текущий момент';

CREATE TABLE IF NOT EXISTS dictionary.account_types
(
    type        VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

COMMENT ON TABLE dictionary.account_types IS 'Справочник типов счетов Т-Банка';
COMMENT ON COLUMN dictionary.account_types.type IS 'Тип счёта';
COMMENT ON COLUMN dictionary.account_types.description IS 'Описание типа счёта на русском языке';

INSERT INTO dictionary.account_types (type, description)
VALUES ('ACCOUNT_TYPE_UNSPECIFIED', 'Тип счёта не определён'),
       ('ACCOUNT_TYPE_TINKOFF', 'Брокерский счёт'),
       ('ACCOUNT_TYPE_TINKOFF_IIS', 'Индивидуальный инвестиционный счёт (ИИС)'),
       ('ACCOUNT_TYPE_INVEST_BOX', 'Инвесткопилка'),
       ('ACCOUNT_TYPE_INVEST_FUND', 'Фонд денежного рынка'),
       ('ACCOUNT_TYPE_DEBIT', 'Дебетовый карточный счёт'),
       ('ACCOUNT_TYPE_SAVING', 'Накопительный счёт'),
       ('ACCOUNT_TYPE_DFA', 'Смарт-счёт (Цифровые активы)');


CREATE TABLE IF NOT EXISTS dictionary.account_statuses
(
    status      VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

COMMENT ON TABLE dictionary.account_statuses IS 'Справочник статусов счетов';
COMMENT ON COLUMN dictionary.account_statuses.status IS 'Статус счёта';
COMMENT ON COLUMN dictionary.account_statuses.description IS 'Описание статуса счёта на русском языке';

INSERT INTO dictionary.account_statuses (status, description)
VALUES ('ACCOUNT_STATUS_UNSPECIFIED', 'Статус счёта не определён'),
       ('ACCOUNT_STATUS_NEW', 'Новый счёт в процессе открытия'),
       ('ACCOUNT_STATUS_OPEN', 'Открытый рабочий счёт'),
       ('ACCOUNT_STATUS_CLOSED', 'Закрытый счёт'),
       ('ACCOUNT_STATUS_ALL', 'Все счета');


CREATE TABLE IF NOT EXISTS dictionary.account_access_levels
(
    level       VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

COMMENT ON TABLE dictionary.account_access_levels IS 'Справочник уровней доступа к счёта';
COMMENT ON COLUMN dictionary.account_access_levels.level IS 'Уровень доступа к счёту';
COMMENT ON COLUMN dictionary.account_access_levels.description IS 'Описание уровня доступа на русском языке';

INSERT INTO dictionary.account_access_levels (level, description)
VALUES ('ACCOUNT_ACCESS_LEVEL_UNSPECIFIED', 'Уровень доступа не определён'),
       ('ACCOUNT_ACCESS_LEVEL_FULL_ACCESS', 'Полный доступ к счёту'),
       ('ACCOUNT_ACCESS_LEVEL_READ_ONLY', 'Доступ с уровнем прав «только чтение»'),
       ('ACCOUNT_ACCESS_LEVEL_NO_ACCESS', 'Нет доступа');

CREATE TABLE IF NOT EXISTS dictionary.tariffs
(
    tariff      VARCHAR(20) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

COMMENT ON TABLE dictionary.tariffs IS 'Тарифы Т-инвестиций';
COMMENT ON COLUMN dictionary.tariffs.tariff IS 'Название тарифа';
COMMENT ON COLUMN dictionary.tariffs.description IS 'Описание тарифа';

INSERT INTO dictionary.tariffs (tariff, description)
VALUES ('investor', 'Тариф «Инвестор»'),
       ('trader', 'Тариф «Трейдер»'),
       ('premium', 'Тариф «Премиум»');

CREATE TABLE IF NOT EXISTS tbank.accounts
(
    id           VARCHAR(36) PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    acc_name     VARCHAR(255) NOT NULL,
    type         VARCHAR(50)  NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    access_level VARCHAR(50)  NOT NULL,
    opened_date  TIMESTAMP    NOT NULL,
    closed_date  TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_sync_at TIMESTAMP,
    CONSTRAINT fk_tbank_accounts_user FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_tbank_accounts_type FOREIGN KEY (type) REFERENCES dictionary.account_types (type),
    CONSTRAINT fk_tbank_accounts_status FOREIGN KEY (status) REFERENCES dictionary.account_statuses (status),
    CONSTRAINT fk_tbank_accounts_level FOREIGN KEY (access_level) REFERENCES dictionary.account_access_levels (level)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_tbank_user_acc_name ON tbank.accounts (user_id, acc_name);

CREATE INDEX IF NOT EXISTS idx_tbank_accounts_user_id ON tbank.accounts (user_id);
CREATE INDEX IF NOT EXISTS idx_tbank_accounts_type ON tbank.accounts (type);
CREATE INDEX IF NOT EXISTS idx_tbank_accounts_status ON tbank.accounts (status);
CREATE INDEX IF NOT EXISTS idx_tbank_accounts_opened_date ON tbank.accounts (opened_date);

COMMENT ON TABLE tbank.accounts IS 'Счета пользователя в Т-Инвестициях';
COMMENT ON COLUMN tbank.accounts.id IS 'ID счёта в системе Т-Инвестиций';
COMMENT ON COLUMN tbank.accounts.acc_name IS 'Название счёта';
COMMENT ON COLUMN tbank.accounts.type IS 'Тип счёта';
COMMENT ON COLUMN tbank.accounts.status IS 'Статус счёта';
COMMENT ON COLUMN tbank.accounts.access_level IS 'Уровень доступа';
COMMENT ON COLUMN tbank.accounts.opened_date IS 'Дата и время открытия счёта';
COMMENT ON COLUMN tbank.accounts.closed_date IS 'Дата и время закрытия счёта';
COMMENT ON COLUMN tbank.accounts.last_sync_at IS 'Дата и время последней синхронизации';

CREATE TABLE IF NOT EXISTS tbank.active_accounts
(
    user_id    BIGINT PRIMARY KEY,
    account_id VARCHAR(36) UNIQUE NOT NULL,
    CONSTRAINT fk_tbank_active_accounts_user FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_tbank_active_accounts_accounts FOREIGN KEY (account_id) REFERENCES tbank.accounts (id) ON DELETE CASCADE
);

COMMENT ON TABLE tbank.active_accounts IS 'Счета пользователей в Т-Инвестициях, используемые в текущий момент';
COMMENT ON COLUMN tbank.active_tokens.user_id IS 'ID пользователя - владельца счёта';
COMMENT ON COLUMN tbank.active_tokens.token_id IS 'ID счёта, используемого в текущий момент';

CREATE TABLE IF NOT EXISTS tbank.users_tariffs
(
    user_id BIGINT PRIMARY KEY,
    tariff  VARCHAR(20) NOT NULL,

    CONSTRAINT fk_tbank_users_tariffs_user FOREIGN KEY (user_id) REFERENCES auth.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_tbank_users_tariffs_tariff FOREIGN KEY (tariff) REFERENCES dictionary.tariffs (tariff)
);

COMMENT ON TABLE tbank.users_tariffs IS 'Тарифы пользователей Т-Инвестиций';
COMMENT ON COLUMN tbank.users_tariffs.user_id IS 'ID пользователя';
COMMENT ON COLUMN tbank.users_tariffs.tariff IS 'Тариф';
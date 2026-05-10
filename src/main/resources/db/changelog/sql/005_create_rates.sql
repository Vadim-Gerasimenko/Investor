CREATE SCHEMA IF NOT EXISTS cbrf;

CREATE TABLE IF NOT EXISTS cbrf.currencies
(
    code_a3  VARCHAR(3) PRIMARY KEY,
    code_n3  VARCHAR(3)  NOT NULL UNIQUE,
    nominal  INTEGER     NOT NULL,
    name_rus VARCHAR(50) NOT NULL,
    name_eng VARCHAR(50) NOT NULL
);

INSERT INTO cbrf.currencies(code_a3, code_n3, nominal, name_rus, name_eng)
VALUES ('RUB', '643', 1, 'Российский рубль', 'Russian ruble');

COMMENT ON TABLE cbrf.currencies IS 'Справочник валют';
COMMENT ON COLUMN cbrf.currencies.code_a3 IS 'Трехзначный буквенный код валюты';
COMMENT ON COLUMN cbrf.currencies.code_n3 IS 'Трехзначный цифровой код валюты';
COMMENT ON COLUMN cbrf.currencies.nominal IS 'Номинал валюты';
COMMENT ON COLUMN cbrf.currencies.name_rus IS 'Название валюты на русском языке';
COMMENT ON COLUMN cbrf.currencies.name_eng IS 'Название валюты на английском языке';

CREATE TABLE IF NOT EXISTS cbrf.rates
(
    currency_from VARCHAR NOT NULL,
    currency_to   VARCHAR NOT NULL,
    rate_nano     BIGINT  NOT NULL,
    start_date    DATE    NOT NULL,

    CONSTRAINT pk_cbrf_rates PRIMARY KEY (currency_from, currency_to, start_date),

    CONSTRAINT fk_сbrf_rates_currency_from
        FOREIGN KEY (currency_from) REFERENCES cbrf.currencies (code_a3) ON DELETE CASCADE,
    CONSTRAINT fk_сbrf_rates_currency_to
        FOREIGN KEY (currency_to) REFERENCES cbrf.currencies (code_a3) ON DELETE CASCADE
);

COMMENT ON TABLE cbrf.rates IS 'Курсы валюты';
COMMENT ON COLUMN cbrf.rates.currency_from IS 'Трехзначный буквенный код валюты, из которой происходит конвертация';
COMMENT ON COLUMN cbrf.rates.currency_to IS 'Трехзначный буквенный код валюты, в которую происходит конвертация';
COMMENT ON COLUMN cbrf.rates.rate IS 'Курс валюты';
COMMENT ON COLUMN cbrf.rates.start_date IS 'Дата старта действия курса';
CREATE TABLE IF NOT EXISTS dictionary.instrument_types
(
    type        VARCHAR(20) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

COMMENT ON TABLE dictionary.instrument_types IS 'Справочник типов финансовых инструментов';
COMMENT ON COLUMN dictionary.instrument_types.type IS 'Тип инструмента';
COMMENT ON COLUMN dictionary.instrument_types.description IS 'Описание на русском языке';

INSERT INTO dictionary.instrument_types (type, description)
VALUES ('bond', 'Облигации'),
       ('share', 'Акции'),
       ('currency', 'Валюта'),
       ('etf', 'ETF (Биржевые фонды)'),
       ('future', 'Фьючерсы'),
       ('precious metal', 'Драгоценные металлы'),
       ('option', 'Опционы'),
       ('', 'Не является инструментом');

CREATE TABLE IF NOT EXISTS dictionary.instruments_fees
(
    tariff          VARCHAR(20) NOT NULL,
    instrument_type VARCHAR(20) NOT NULL,
    percent_nano    BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_instruments_fees PRIMARY KEY (tariff, instrument_type),

    CONSTRAINT fk_instruments_fees_tariff
        FOREIGN KEY (tariff) REFERENCES dictionary.tariffs (tariff) ON DELETE CASCADE,
    CONSTRAINT fk_instruments_fees_instrument_type
        FOREIGN KEY (instrument_type) REFERENCES dictionary.instrument_types (type) ON DELETE CASCADE,
    CONSTRAINT unique_tariff_instrument UNIQUE (tariff, instrument_type)
);


COMMENT ON TABLE dictionary.instruments_fees IS 'Комиссии Т-Инвестиций по инструментам';
COMMENT ON COLUMN dictionary.instruments_fees.tariff IS 'Тариф';
COMMENT ON COLUMN dictionary.instruments_fees.instrument_type IS 'Тип инструмента';
COMMENT ON COLUMN dictionary.instruments_fees.percent_nano IS 'Процент комиссии в нано-единицах';

INSERT INTO dictionary.instruments_fees (tariff, instrument_type, percent_nano)
VALUES ('investor', 'bond', 300000000),
       ('investor', 'share', 300000000),
       ('investor', 'etf', 300000000),
       ('investor', 'currency', 900000000),
       ('investor', 'precious metal', 1900000000),
       ('investor', 'future', 100000000),
       ('investor', 'option', 300000000),
       ('trader', 'bond', 50000000),
       ('trader', 'share', 50000000),
       ('trader', 'etf', 50000000),
       ('trader', 'currency', 500000000),
       ('trader', 'precious metal', 1500000000),
       ('trader', 'future', 40000000),
       ('trader', 'option', 200000000),
       ('premium', 'bond', 40000000),
       ('premium', 'share', 40000000),
       ('premium', 'etf', 40000000),
       ('premium', 'currency', 400000000),
       ('premium', 'precious metal', 900000000),
       ('premium', 'future', 20000000),
       ('premium', 'option', 150000000);

CREATE TABLE IF NOT EXISTS tbank.instruments
(
    uid             VARCHAR(36) PRIMARY KEY,
    figi            VARCHAR(30)  NOT NULL,
    ticker          VARCHAR(20),
    isin            VARCHAR(20),
    lot             INTEGER      NOT NULL DEFAULT 1,
    currency        VARCHAR(3)   NOT NULL,
    name            VARCHAR(255) NOT NULL,
    instrument_type VARCHAR(20),

    FOREIGN KEY (instrument_type) REFERENCES dictionary.instrument_types (type)
);

COMMENT ON TABLE tbank.instruments IS 'Справочник финансовых инструментов';
COMMENT ON COLUMN tbank.instruments.uid IS 'Уникальный ID инструмента';
COMMENT ON COLUMN tbank.instruments.figi IS 'FIGI-идентификатор';
COMMENT ON COLUMN tbank.instruments.lot IS 'Размер лота';
COMMENT ON COLUMN tbank.instruments.instrument_type IS 'Тип инструмента';

CREATE TABLE IF NOT EXISTS tbank.instrument_prices
(
    instrument_uid VARCHAR(36) PRIMARY KEY,
    price          BIGINT    NOT NULL,
    recorded_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instrument_uid) REFERENCES tbank.instruments (uid) ON DELETE CASCADE
);

COMMENT ON TABLE tbank.instrument_prices IS 'Текущие цены инструментов';
COMMENT ON COLUMN tbank.instrument_prices.instrument_uid IS 'UID инструмента';
COMMENT ON COLUMN tbank.instrument_prices.price IS 'Цена в нано-единицах';
COMMENT ON COLUMN tbank.instrument_prices.recorded_at IS 'Дата и время последнего обновления цены';


CREATE TABLE IF NOT EXISTS dictionary.operation_states
(
    state       VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

COMMENT ON TABLE dictionary.operation_states IS 'Справочник состояний операций';
COMMENT ON COLUMN dictionary.operation_states.state IS 'Состояние';
COMMENT ON COLUMN dictionary.operation_states.description IS 'Описание состояния на русском языке';

INSERT INTO dictionary.operation_states (state, description)
VALUES ('OPERATION_STATE_UNSPECIFIED', 'Статус операции не определён'),
       ('OPERATION_STATE_EXECUTED', 'Исполнена частично или полностью'),
       ('OPERATION_STATE_CANCELED', 'Операция отменена'),
       ('OPERATION_STATE_PROGRESS', 'В процессе исполнения');

CREATE TABLE IF NOT EXISTS dictionary.operation_types
(
    type        VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    category    VARCHAR(50)
);

COMMENT ON TABLE dictionary.operation_types IS 'Справочник типов операций';
COMMENT ON COLUMN dictionary.operation_types.type IS 'Тип операции';
COMMENT ON COLUMN dictionary.operation_types.description IS 'Описание на русском языке';
COMMENT ON COLUMN dictionary.operation_types.category IS 'Категория';

INSERT INTO dictionary.operation_types (type, description, category)
VALUES ('OPERATION_TYPE_UNSPECIFIED', 'Тип операции не определен', 'OTHER'),
       ('OPERATION_TYPE_INPUT', 'Пополнение брокерского счета', 'INCOME'),
       ('OPERATION_TYPE_BOND_TAX', 'Удержание НДФЛ по купонам', 'EXPENSE'),
       ('OPERATION_TYPE_OUTPUT_SECURITIES', 'Вывод ЦБ', 'EXPENSE'),
       ('OPERATION_TYPE_OVERNIGHT', 'Доход по сделке РЕПО овернайт', 'INCOME'),
       ('OPERATION_TYPE_TAX', 'Удержание налога', 'EXPENSE'),
       ('OPERATION_TYPE_BOND_REPAYMENT_FULL', 'Полное погашение облигаций', 'INCOME'),
       ('OPERATION_TYPE_SELL_CARD', 'Продажа ЦБ с карты', 'INCOME'),
       ('OPERATION_TYPE_DIVIDEND_TAX', 'Удержание налога по дивидендам', 'EXPENSE'),
       ('OPERATION_TYPE_OUTPUT', 'Вывод денежных средств', 'EXPENSE'),
       ('OPERATION_TYPE_BOND_REPAYMENT', 'Частичное погашение облигаций', 'INCOME'),
       ('OPERATION_TYPE_TAX_CORRECTION', 'Корректировка налога', 'EXPENSE'),
       ('OPERATION_TYPE_SERVICE_FEE', 'Удержание комиссии за обслуживание брокерского счета', 'EXPENSE'),
       ('OPERATION_TYPE_BENEFIT_TAX', 'Удержание налога за материальную выгоду', 'EXPENSE'),
       ('OPERATION_TYPE_MARGIN_FEE', 'Удержание комиссии за непокрытую позицию', 'EXPENSE'),
       ('OPERATION_TYPE_BUY', 'Покупка ЦБ', 'EXPENSE'),
       ('OPERATION_TYPE_BUY_CARD', 'Покупка ЦБ с карты', 'EXPENSE'),
       ('OPERATION_TYPE_INPUT_SECURITIES', 'Перевод ценных бумаг из другого депозитария', 'INCOME'),
       ('OPERATION_TYPE_SELL_MARGIN', 'Продажа в результате Margin-call', 'INCOME'),
       ('OPERATION_TYPE_BROKER_FEE', 'Удержание комиссии за операцию', 'EXPENSE'),
       ('OPERATION_TYPE_BUY_MARGIN', 'Покупка в результате Margin-call', 'EXPENSE'),
       ('OPERATION_TYPE_DIVIDEND', 'Выплата дивидендов', 'INCOME'),
       ('OPERATION_TYPE_SELL', 'Продажа ЦБ', 'INCOME'),
       ('OPERATION_TYPE_COUPON', 'Выплата купонов', 'INCOME'),
       ('OPERATION_TYPE_SUCCESS_FEE', 'Удержание комиссии SuccessFee', 'EXPENSE'),
       ('OPERATION_TYPE_DIVIDEND_TRANSFER', 'Передача дивидендного дохода', 'TRANSFER'),
       ('OPERATION_TYPE_ACCRUING_VARMARGIN', 'Зачисление вариационной маржи', 'INCOME'),
       ('OPERATION_TYPE_WRITING_OFF_VARMARGIN', 'Списание вариационной маржи', 'EXPENSE'),
       ('OPERATION_TYPE_DELIVERY_BUY', 'Покупка в рамках экспирации фьючерсного контракта', 'EXPENSE'),
       ('OPERATION_TYPE_DELIVERY_SELL', 'Продажа в рамках экспирации фьючерсного контракта', 'INCOME'),
       ('OPERATION_TYPE_TRACK_MFEE', 'Комиссия за управление по счету автоследования', 'EXPENSE'),
       ('OPERATION_TYPE_TRACK_PFEE', 'Комиссия за результат по счету автоследования', 'EXPENSE'),
       ('OPERATION_TYPE_TAX_PROGRESSIVE', 'Удержание налога по ставке 15%', 'EXPENSE'),
       ('OPERATION_TYPE_BOND_TAX_PROGRESSIVE', 'Удержание налога по купонам по ставке 15%', 'EXPENSE'),
       ('OPERATION_TYPE_DIVIDEND_TAX_PROGRESSIVE', 'Удержание налога по дивидендам по ставке 15%', 'EXPENSE'),
       ('OPERATION_TYPE_BENEFIT_TAX_PROGRESSIVE', 'Удержание налога за материальную выгоду по ставке 15%', 'EXPENSE'),
       ('OPERATION_TYPE_TAX_CORRECTION_PROGRESSIVE', 'Корректировка налога по ставке 15%', 'EXPENSE'),
       ('OPERATION_TYPE_TAX_REPO_PROGRESSIVE', 'Удержание налога за возмещение по сделкам РЕПО по ставке 15%',
        'EXPENSE'),
       ('OPERATION_TYPE_TAX_REPO', 'Удержание налога за возмещение по сделкам РЕПО', 'EXPENSE'),
       ('OPERATION_TYPE_TAX_REPO_HOLD', 'Удержание налога по сделкам РЕПО', 'EXPENSE'),
       ('OPERATION_TYPE_TAX_REPO_REFUND', 'Возврат налога по сделкам РЕПО', 'INCOME'),
       ('OPERATION_TYPE_TAX_REPO_HOLD_PROGRESSIVE', 'Удержание налога по сделкам РЕПО по ставке 15%', 'EXPENSE'),
       ('OPERATION_TYPE_TAX_REPO_REFUND_PROGRESSIVE', 'Возврат налога по сделкам РЕПО по ставке 15%', 'INCOME'),
       ('OPERATION_TYPE_DIV_EXT', 'Выплата дивидендов на карту', 'INCOME'),
       ('OPERATION_TYPE_TAX_CORRECTION_COUPON', 'Корректировка налога по купонам', 'EXPENSE'),
       ('OPERATION_TYPE_CASH_FEE', 'Комиссия за валютный остаток', 'EXPENSE'),
       ('OPERATION_TYPE_OUT_FEE', 'Комиссия за вывод валюты с брокерского счета', 'EXPENSE'),
       ('OPERATION_TYPE_OUT_STAMP_DUTY', 'Гербовый сбор', 'EXPENSE'),
       ('OPERATION_TYPE_OUTPUT_SWIFT', 'SWIFT-перевод', 'EXPENSE'),
       ('OPERATION_TYPE_INPUT_SWIFT', 'SWIFT-перевод', 'INCOME'),
       ('OPERATION_TYPE_OUTPUT_ACQUIRING', 'Перевод на карту', 'EXPENSE'),
       ('OPERATION_TYPE_INPUT_ACQUIRING', 'Перевод с карты', 'INCOME'),
       ('OPERATION_TYPE_OUTPUT_PENALTY', 'Комиссия за вывод средств', 'EXPENSE'),
       ('OPERATION_TYPE_ADVICE_FEE', 'Списание оплаты за сервис Советов', 'EXPENSE'),
       ('OPERATION_TYPE_TRANS_IIS_BS', 'Перевод ценных бумаг с ИИС на брокерский счет', 'TRANSFER'),
       ('OPERATION_TYPE_TRANS_BS_BS', 'Перевод ценных бумаг с одного брокерского счета на другой', 'TRANSFER'),
       ('OPERATION_TYPE_OUT_MULTI', 'Вывод денежных средств со счета', 'EXPENSE'),
       ('OPERATION_TYPE_INP_MULTI', 'Пополнение денежных средств со счета', 'INCOME'),
       ('OPERATION_TYPE_OVER_PLACEMENT', 'Размещение биржевого овернайта', 'EXPENSE'),
       ('OPERATION_TYPE_OVER_COM', 'Списание комиссии', 'EXPENSE'),
       ('OPERATION_TYPE_OVER_INCOME', 'Доход от оверанайта', 'INCOME'),
       ('OPERATION_TYPE_OPTION_EXPIRATION', 'Экспирация опциона', 'EXPENSE'),
       ('OPERATION_TYPE_FUTURE_EXPIRATION', 'Экспирация фьючерса', 'EXPENSE'),
       ('OPERATION_TYPE_OTHER_FEE', 'Прочие комиссии', 'EXPENSE'),
       ('OPERATION_TYPE_OTHER', 'Операция по счету', 'OTHER'),
       ('OPERATION_TYPE_DFA_REDEMPTION', 'Погашение ЦФА-токена', 'INCOME'),
       ('OPERATION_TYPE_PRIMARY_ORDER', 'Отмена заявки на первичное размещение по ЦФА', 'EXPENSE');

CREATE TABLE tbank.operations
(
    id                  VARCHAR(36) PRIMARY KEY,
    parent_operation_id VARCHAR(36),
    account_id          VARCHAR(36) NOT NULL,
    currency            VARCHAR(3)  NOT NULL,
    operation_type      VARCHAR(50) NOT NULL,
    state               VARCHAR(30) NOT NULL,
    payment_value       BIGINT      NOT NULL DEFAULT 0,
    quantity            BIGINT,
    quantity_rest       BIGINT,
    instrument_uid      VARCHAR(36),
    operation_date      TIMESTAMP   NOT NULL,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (account_id) REFERENCES tbank.accounts (id),
    FOREIGN KEY (operation_type) REFERENCES dictionary.operation_types (type),
    FOREIGN KEY (state) REFERENCES dictionary.operation_states (state),
    FOREIGN KEY (instrument_uid) REFERENCES tbank.instruments (uid) ON DELETE SET NULL,
    FOREIGN KEY (parent_operation_id) REFERENCES tbank.operations (id) ON DELETE SET NULL
);

CREATE INDEX idx_tbank_operations_account_id ON tbank.operations (account_id);
CREATE INDEX idx_tbank_operations_instrument_uid ON tbank.operations (instrument_uid);
CREATE INDEX idx_tbank_operations_date ON tbank.operations (operation_date);
CREATE INDEX idx_tbank_operations_type ON tbank.operations (operation_type);

CREATE INDEX idx_tbank_operations_account_date ON tbank.operations (account_id, operation_date DESC);
CREATE INDEX idx_tbank_operations_parent ON tbank.operations (parent_operation_id);

COMMENT ON COLUMN tbank.operations.parent_operation_id IS 'ID родительской операции';
COMMENT ON COLUMN tbank.operations.payment_value IS 'Сумма платежа в нано-единицах';
COMMENT ON COLUMN tbank.operations.quantity IS 'Количество инструментов';
COMMENT ON COLUMN tbank.operations.operation_date IS 'Дата и время совершения операции';

COMMENT ON TABLE tbank.operations IS 'Операции по счетам Т-Инвестиций';
COMMENT ON COLUMN tbank.operations.id IS 'Уникальный идентификатор операции';
COMMENT ON COLUMN tbank.operations.account_id IS 'ID счёта в системе';
COMMENT ON COLUMN tbank.operations.currency IS 'Валюта операции (3-х буквенный код)';
COMMENT ON COLUMN tbank.operations.operation_type IS 'Тип операции';
COMMENT ON COLUMN tbank.operations.state IS 'Состояние операции';
COMMENT ON COLUMN tbank.operations.payment_value IS 'Сумма платежа в нано-единицах';
COMMENT ON COLUMN tbank.operations.quantity IS 'Количество инструментов в операции';
COMMENT ON COLUMN tbank.operations.quantity_rest IS 'Неисполненный остаток по сделке';
COMMENT ON COLUMN tbank.operations.instrument_uid IS 'UID инструмента';
COMMENT ON COLUMN tbank.operations.operation_date IS 'Дата и время совершения операции';
COMMENT ON COLUMN tbank.operations.created_at IS 'Дата и время создания записи в системе';

CREATE TABLE tbank.trades
(
    trade_id       VARCHAR(36) NOT NULL PRIMARY KEY,
    operation_id   VARCHAR(36) NOT NULL,
    trade_date     TIMESTAMP   NOT NULL,
    quantity       BIGINT      NOT NULL,
    price_value    BIGINT      NOT NULL,
    price_currency VARCHAR(3),
    FOREIGN KEY (operation_id) REFERENCES tbank.operations (id) ON DELETE CASCADE
);

COMMENT ON TABLE tbank.trades IS 'Сделки в составе операций';
COMMENT ON COLUMN tbank.trades.trade_id IS 'ID сделки в торговой системе';
COMMENT ON COLUMN tbank.trades.price_value IS 'Цена сделки в нано-единицах';

CREATE INDEX idx_tbank_trades_operation_id ON tbank.trades (operation_id);
CREATE INDEX idx_tbank_trades_trade_date ON tbank.trades (trade_date DESC);
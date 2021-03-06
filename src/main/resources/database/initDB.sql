CREATE TABLE IF NOT EXISTS user
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    money INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS offer
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    count INT NOT NULL,
    price INT NOT NULL,
    available_count INT NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE CASCADE ON DELETE SET NULL,
    type ENUM('BUY', 'SELL')
);

CREATE TABLE IF NOT EXISTS transaction
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_date_time TIMESTAMP(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS deal
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sell_offer_id BIGINT,
    FOREIGN KEY (sell_offer_id) REFERENCES offer (id) ON UPDATE CASCADE ON DELETE SET NULL,
    buy_offer_id BIGINT,
    FOREIGN KEY (buy_offer_id) REFERENCES offer (id) ON UPDATE CASCADE ON DELETE SET NULL,
    count INT NOT NULL,
    price INT NOT NULL,
    transaction_id BIGINT,
    FOREIGN KEY (transaction_id) REFERENCES transaction (id) ON UPDATE CASCADE ON DELETE SET NULL
);


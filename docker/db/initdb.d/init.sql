-- # Create
-- 코드-은행
CREATE TABLE code_bank (
    bank_code VARCHAR(10) PRIMARY KEY,
    bank_name VARCHAR(50),
    account_format VARCHAR(50),
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    upt_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 코드-거래 유형
CREATE TABLE code_transaction (
    transaction_code VARCHAR(10) PRIMARY KEY,
    description VARCHAR(50),
    daily_limit DECIMAL(15, 2),
    fee_percent DECIMAL(5, 2),
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    upt_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 계좌
CREATE TABLE account (
    account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bank_code VARCHAR(10) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    balance DECIMAL(15, 2),
    del_yn TINYINT(1) DEFAULT 0,
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    upt_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_a_cb_bank_code FOREIGN KEY (bank_code) REFERENCES code_bank(bank_code)
);

CREATE INDEX idx_a_bank_code_account_number ON account(bank_code, account_number);

-- 계좌-거래
CREATE TABLE account_transaction (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code VARCHAR(10) NOT NULL,
    account_id BIGINT NOT NULL,
    target_account_id BIGINT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    upt_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_at_ct_transaction_code FOREIGN KEY (transaction_code) REFERENCES code_transaction(transaction_code),
    CONSTRAINT fk_at_a_account_id FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE ,
    CONSTRAINT fk_at_target_account_id FOREIGN KEY (target_account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

-- 계좌-한도
CREATE TABLE account_limit (
    account_id BIGINT,
    transaction_code VARCHAR(10),
    daily_amount DECIMAL(15, 2),
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    upt_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id, transaction_code),
    CONSTRAINT fk_al_a_account_id FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE ,
    CONSTRAINT fk_al_ct_transaction_code FOREIGN KEY (transaction_code) REFERENCES code_transaction(transaction_code)
);

-- 거래-수수료 테이블 생성
CREATE TABLE transaction_fee (
    transaction_id BIGINT PRIMARY KEY,
    fee DECIMAL(15, 2) NOT NULL,
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    upt_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_atf_at_transaction_id FOREIGN KEY (transaction_id) REFERENCES account_transaction(transaction_id)
        ON DELETE CASCADE
);


-- # insert
-- 코드-은행
INSERT INTO code_bank (bank_code, bank_name, account_format) VALUES
('001', '한국은행', '###############'),
('002', '산업은행', '###############'),
('003', '기업은행', '###-##-######'),
('004', '국민은행', '######-##-######'),
('005', '외환은행', '###############'),
('007', '수협중앙회', '###-######-##'),
('008', '수출입은행', '###############'),
('011', '농협은행', '###-####-####-##'),
('012', '지역농.축협', '###############'),
('020', '우리은행', '####-###-######'),
('023', 'SC은행', '###-######-##'),
('027', '한국씨티은행', '###-######-##'),
('031', '대구은행', '###############'),
('032', '부산은행', '###-######-###'),
('034', '광주은행', '###############'),
('035', '제주은행', '###############'),
('037', '전북은행', '###############'),
('039', '경남은행', '###############'),
('045', '새마을금고중앙회', '###############'),
('048', '신협중앙회', '###############'),
('050', '상호저축은행', '###-##-######'),
('051', '중국은행', '###############'),
('052', '모건스탠리은행', '###############'),
('054', 'HSBC은행', '###############'),
('055', '도이치은행', '###############'),
('056', '알비에스피엘씨은행', '###############'),
('057', '제이피모간체이스은행', '###############'),
('058', '미즈호은행', '###############'),
('059', '미쓰비시도쿄UFJ은행', '###############'),
('060', 'BOA은행', '###############'),
('061', '비엔피파리바은행', '###############'),
('062', '중국공상은행', '###############'),
('063', '중국은행', '###############'),
('064', '산림조합중앙회', '###############'),
('065', '대화은행', '###############'),
('066', '교통은행', '###############'),
('071', '우체국', '######-######'),
('076', '신용보증기금', '###############'),
('077', '기술보증기금', '###############'),
('081', 'KEB하나은행', '###-######-###'),
('088', '신한은행', '###-######-###'),
('089', '케이뱅크', '###-######-###'),
('090', '카카오뱅크', '###-######-###'),
('092', '토스뱅크', '####-####-####'),
('093', '한국주택금융공사', '###############'),
('094', '서울보증보험', '###############'),
('095', '경찰청', '###############'),
('096', '한국전자금융(주)', '###############'),
('099', '금융결제원', '###############'),
('209', '유안타증권', '###############'),
('218', '현대증권', '###############'),
('221', '골든브릿지투자증권', '###############'),
('222', '한양증권', '###############'),
('223', '리딩투자증권', '###############'),
('224', 'BNK투자증권', '###############'),
('225', 'IBK투자증권', '###############'),
('226', 'KB투자증권', '###############'),
('227', 'KTB투자증권', '###############'),
('230', '미래에셋증권', '###############'),
('238', '대우증권', '###############'),
('240', '삼성증권', '###############'),
('243', '한국투자증권', '###############'),
('261', '교보증권', '###############'),
('262', '하이투자증권', '###############'),
('263', 'HMC투자증권', '###############'),
('264', '키움증권', '###############'),
('265', '이베스트투자증권', '###############'),
('266', 'SK증권', '###############'),
('267', '대신증권', '###############'),
('269', '한화투자증권', '###############'),
('270', '하나대투증권', '###############'),
('278', '신한금융투자', '###############'),
('279', 'DB금융투자', '###############'),
('280', '유진투자증권', '###############'),
('287', '메리츠종합금융증권', '###############'),
('289', 'NH투자증권', '###############'),
('290', '부국증권', '###############'),
('291', '신영증권', '###############'),
('292', '엘아이지투자증권', '###############'),
('293', '한국증권금융', '###############'),
('294', '펀드온라인코리아', '###############'),
('295', '우리종합금융', '###############'),
('296', '삼성선물', '###############'),
('297', '외환선물', '###############'),
('298', '현대선물', '###############');

-- 코드-거래 유형
INSERT INTO code_transaction (transaction_code, description, daily_limit, fee_percent) VALUES
('00', '입금', 0, 0),
('01', '출금', 1000000, 0),
('02', '이체', 3000000, 1);


-- # insert demo
-- 계좌
INSERT INTO account (account_id, bank_code, account_number, balance, del_yn, reg_date, upt_date) VALUES
(1, '003', '6F9+GB/Hb3GIxxxOR2/M+A==', 6890000.00, 0, '2025-02-25 14:13:48', '2025-02-25 14:15:11'),
(2, '004', 'NDJhNpRUU/kyziVs0E8gtHDOnU/RL1PLxllLw/Lpfog=', 475000.00, 0, '2025-02-25 14:13:50', '2025-02-25 14:15:33'),
(3, '007', 'MCv+6ZIEZUUaeasB92L3Lg==', 2489900.00, 0, '2025-02-25 14:13:57', '2025-02-25 14:17:21'),
(4, '008', '+GWyimasIgqXLsdSFQ1O+A==', 10000.00, 0, '2025-02-25 14:16:19', '2025-02-25 23:18:34');

-- 계좌-한도
INSERT INTO account_limit (account_id, transaction_code, daily_amount, reg_date, upt_date) VALUES
(1, '00', 0.00, '2025-02-25 14:13:49', null),
(1, '01', 0.00, '2025-02-25 14:13:49', '2025-02-26 14:15:11'),
(1, '02', 0.00, '2025-02-25 14:13:49', '2025-02-26 14:14:53'),
(2, '00', 0.00, '2025-02-25 14:13:50', null),
(2, '01', 0.00, '2025-02-25 14:13:50', null),
(2, '02', 0.00, '2025-02-25 14:13:50', '2025-02-26 14:15:33'),
(3, '00', 0.00, '2025-02-25 14:13:57', null),
(3, '01', 0.00, '2025-02-25 14:13:57', null),
(3, '02', 0.00, '2025-02-25 14:13:57', '2025-02-26 14:17:21'),
(4, '00', 0.00, '2025-02-25 14:16:19', null),
(4, '01', 0.00, '2025-02-25 14:16:19', null),
(4, '02', 0.00, '2025-02-25 14:16:19', null);

-- 계좌-거래
INSERT INTO account_transaction (transaction_id, transaction_code, account_id, target_account_id, amount, reg_date, upt_date) VALUES
(1, '00', 1, null, 10000000.00, '2025-02-25 14:14:08', null),
(2, '02', 1, 2, 3000000.00, '2025-02-25 14:14:53', null),
(3, '01', 1, null, 50000.00, '2025-02-25 14:15:08', null),
(4, '01', 1, null, 30000.00, '2025-02-25 14:15:11', null),
(5, '02', 2, 3, 1000000.00, '2025-02-25 14:15:27', null),
(6, '02', 2, 3, 1500000.00, '2025-02-25 14:15:33', null),
(7, '02', 3, 4, 10000.00, '2025-02-25 14:17:21', null);

-- 거래-수수료
INSERT INTO transaction_fee (transaction_id, fee, reg_date, upt_date) values
(2, 30000.00, '2025-02-25 14:14:53', null),
(5, 10000.00, '2025-02-25 14:15:27', null),
(6, 15000.00, '2025-02-25 14:15:33', null),
(7, 100.00, '2025-02-25 14:17:21', null);

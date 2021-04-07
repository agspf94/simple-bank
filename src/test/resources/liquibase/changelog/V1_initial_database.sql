CREATE TABLE bank (
    id INT AUTO_INCREMENT NOT NULL,
    accountNumber INT NOT NULL,
    balance DOUBLE NOT NULL,
    CONSTRAINT PK_BANK PRIMARY KEY (id)
);

INSERT INTO bank VALUES (null, 1, 100.0),
                        (null, 2, 100.0),
                        (null, 3, 100.0),
                        (null, 4, 100.0);
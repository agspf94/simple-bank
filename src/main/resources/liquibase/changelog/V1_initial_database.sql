CREATE TABLE bank (
    id INT AUTO_INCREMENT NOT NULL,
    accountNumber INT NOT NULL,
    balance DOUBLE NOT NULL,
    owner VARCHAR(255),
    CONSTRAINT PK_BANK PRIMARY KEY (id)
);

INSERT INTO bank VALUES (1, 1, 100.0, 'Anderson'),
                        (2, 2, 100.0, 'Giuseppe'),
                        (3, 3, 100.0, 'Saraiva'),
                        (4, 4, 100.0, 'Patriarca');
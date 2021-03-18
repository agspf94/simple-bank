create table bank (
                      id bigint generated by default as identity,
                      account_number integer not null,
                      owner varchar(255),
                      balance double not null,
                      primary key (id)
);

insert into bank values (1, 1, 100.0, 'Anderson'),
                        (2, 2, 100.0, 'Giuseppe'),
                        (3, 3, 100.0, 'Saraiva'),
                        (4, 4, 100.0, 'Patriarca');
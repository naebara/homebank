drop table IF EXISTS customer;

create TABLE customer (
    id bigint auto_increment PRIMARY KEY NOT NULL,
    fullName varchar(250),
    address varchar(250),
    phone_number varchar(250),
    ssn varchar(33)
);

insert into customer (fullName, address, phone_number, ssn ) values ('Dan Badea1', 'Mures', '2224445558', '343-25-5859');
insert into customer (fullName, address, phone_number, ssn ) values ('Sergiu Gal1', 'Satu Mare', '46645345', '354-12-7742');

drop table if exists accounts;

create table accounts(
    id bigint auto_increment PRIMARY KEY NOT NULL,
    iban varchar(34),
    currency varchar(6),
    amount double,
    customer_id integer,
    issued_at date
);

insert into accounts(iban, currency, amount, customer_id, issued_at) values ('GB82WEST12345698765432', 'EUR', 20, 1, '2022-05-07');
insert into accounts(iban, currency, amount, customer_id, issued_at) values ('GB03BARC20038041157768', 'RON', 10, 1, '2022-05-22');
drop table if exists accounts;
drop table IF EXISTS customer;

create TABLE customer (
    id serial primary key NOT NULL,
    fullName char(20),
    address char(50),
    phone_number char(10),
    ssn char(11)
);

insert into customer (fullName, address, phone_number, ssn ) values ('Florin Dumitrescu', 'Oradea', '2224445558', '343-25-5859');
insert into customer (fullName, address, phone_number, ssn ) values ('Sorin Bontea', 'Satu Mare', '46645345', '354-12-7742');
insert into customer (fullName, address, phone_number, ssn ) values ('Catalin Scarlatescu', 'Bucuresti', '46645345', '354-12-7742');


create table accounts(
    id serial primary key not null,
    iban char(34),
    currency char(6),
    amount numeric,
    customer_id integer,
    issued_at date,
    constraint fk_customer foreign key(customer_id) references customer(id)
);

insert into accounts(iban, currency, amount, customer_id, issued_at) values ('GB82WEST12345698765432', 'EUR', 444, 1, '2022-05-07');
insert into accounts(iban, currency, amount, customer_id, issued_at) values ('GB03BARC20038041157768', 'RON', 200, 1, '2022-05-22');
insert into accounts(iban, currency, amount, customer_id, issued_at) values ('GB23BARC20039541126414', 'RON', 3500, 2, '2022-05-22');
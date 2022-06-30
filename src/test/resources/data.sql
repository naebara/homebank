drop table IF EXISTS customer;

create TABLE customer (
    ID bigint auto_increment PRIMARY KEY NOT NULL,
    fullName varchar(250),
    address varchar(50),
    phone_number varchar(10),
    ssn varchar(11)
);
--
insert into customer (fullName, address, phone_number, ssn ) values ('Dan Badea', 'Oradea', '2224445558', '343-25-5859');
insert into customer (fullName, address, phone_number, ssn ) values ('Sergiu Gal', 'Satu Mare', '46645345', '354-12-7742');
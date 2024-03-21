-- sudo mysql -u root -p
-- creating a database

create database project;

-- log in to the database

use project;

-- creating authors table

create table Authors (
    author_id int not null auto_increment,
    name varchar(100) not null,
    primary key (author_id)
);

-- creating book types table

create table BookTypes (
    type_id int not null auto_increment,
    type_name varchar(100) not null,
    added_percentage decimal(5, 2) not null,
    primary key (type_id)
);

-- creating books table (remove the purchased column if we are deleting books)

create table Books (
    book_id int not null auto_increment,
    isbn decimal(13) not null,
    title varchar(100) not null,
    author_id int not null,
    type_id int not null,
    price decimal(5, 2) not null,
    purchased boolean default false,
    primary key (book_id, isbn),
    foreign key (author_id) references Authors(author_id),
    foreign key (type_id) references BookTypes(type_id)
);

-- creating members table

create table Members (
    member_id int not null auto_increment,
    name varchar(100) not null,
    email varchar(100) not null,
    phone varchar(20) not null,
    points int default 0,
    primary key (member_id)
);

-- inserting into authors table

insert into Authors (name) values ('J.K. Rowling');
insert into Authors (name) values ('J.R.R. Tolkien');
insert into Authors (name) values ('George R.R. Martin');

-- inserting into book types table

insert into BookTypes (type_name, added_percentage) values ('Hard Cover', 0.00);
insert into BookTypes (type_name, added_percentage) values ('Soft Cover', 0.05);
insert into BookTypes (type_name, added_percentage) values ('Cover Lamination', 0.10);

-- inserting into books table

insert into Books (isbn, title, author_id, type_id, price) values (1234567890123, 'The Lord of the Rings', 2, 1, 25.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890123, 'The Lord of the Rings', 2, 1, 25.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890123, 'The Lord of the Rings', 2, 1, 25.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890123, 'The Lord of the Rings', 2, 1, 25.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890124, 'The Hobbit', 2, 2, 20.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890125, 'A Game of Thrones', 3, 1, 30.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890126, 'A Clash of Kings', 3, 2, 35.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890127, 'Harry Potter', 1, 3, 40.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890128, 'Harry Potter', 1, 3, 40.00);
insert into Books (isbn, title, author_id, type_id, price) values (1234567890129, 'A Ring of Death', 2, 3, 40.00);

-- inserting into members table

insert into Members (name, email, phone) values ('John Doe', 'john@abc.com', '123-456-7890');
insert into Members (name, email, phone) values ('Jane Doe', 'jane@abc.com', '123-456-7891');

-- searching number of copies of a book by title

select title as name, price as price, count(*) as copies from Books where title like '%Ring%' group by isbn;

-- searching number of copies of a book by title (where instead of deleting books, we mark them as purchased)

select title as name, price as price, count(*) as copies from Books where title like '%Ring%' and purchased = false group by isbn;

-- searching of copies of a book by isbn

select title as name, price as price, count(*) as copies from Books where isbn = 1234567890123;

-- searching of copies of a book by isbn (where instead of deleting books, we mark them as purchased)

select title as name, price as price, count(*) as copies from Books where isbn = 1234567890123 and purchased = false;

-- searching book_id of a book

select book_id from Books where isbn = 1234567890123;

-- purchasing a book

delete from Books where isbn = 1234567890123 and book_id = 1;

-- purchasing a book (where instead of deleting books, we mark them as purchased)

update Books set purchased = true where isbn = 1234567890123 and book_id = 1;

-- searching for purchased books if we are not deleting them

select title as name, price as price, count(*) as copies from Books where purchased = true group by isbn;
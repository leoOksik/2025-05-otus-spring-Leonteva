insert into authors(full_name) values ('Author_1');
insert into authors(full_name) values ('Author_2');
insert into authors(full_name) values ('Author_3');
insert into authors(full_name) values ('Author_4');

insert into genres(name) values ('Genre_1');
insert into genres(name) values ('Genre_2');
insert into genres(name) values ('Genre_3');
insert into genres(name) values ('Genre_4');
insert into genres(name) values ('Genre_5');
insert into genres(name) values ('Genre_6');

insert into books(title, author_id) values ('BookTitle_1', 1);
insert into books(title, author_id) values ('BookTitle_2', 2);
insert into books(title, author_id) values ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id) values (1, 1);
insert into books_genres(book_id, genre_id) values (1, 2);
insert into books_genres(book_id, genre_id) values (2, 3);
insert into books_genres(book_id, genre_id) values (2, 4);
insert into books_genres(book_id, genre_id) values (3, 5);
insert into books_genres(book_id, genre_id) values (3, 6);

insert into comments(text, book_id) values ('Comment_1_1', 1);
insert into comments(text, book_id) values ('Comment_1_2', 1);
insert into comments(text, book_id) values ('Comment_2_1', 2);
insert into comments(text, book_id) values ('Comment_3_1', 3);

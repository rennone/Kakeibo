CREATE TABLE Categories(
_id integer PRIMARY KEY,
name TEXT UNIQUE
)
/
CREATE TABLE SubCategories(
_id	integer PRIMARY KEY,
name TEXT,
parent TEXT
)
/
INSERT INTO Categories
(_id, name)
VALUES(0, "�H��")
/
INSERT INTO Categories
(_id, name)
VALUES(1, "��ʔ�")
/
INSERT INTO Categories
(_id, name)
VALUES(2, "����")
/
INSERT INTO Categories
(_id, name)
VALUES(3, "���̑�")
/
insert into SubCategories
(_id, name, parent)
values(0,"�Z�p��", "����")
/
insert into SubCategories
(_id, name, parent)
values(1,"����", "����")
/
insert into SubCategories
(_id, name, parent)
values(2,"����", "����")
/
insert into SubCategories
(_id, name, parent)
values(3,"���C�g�m�x��","����")


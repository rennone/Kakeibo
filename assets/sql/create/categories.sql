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
VALUES(0, "H”ï")
/
INSERT INTO Categories
(_id, name)
VALUES(1, "Œğ’Ê”ï")
/
INSERT INTO Categories
(_id, name)
VALUES(2, "‘Ğ")
/
INSERT INTO Categories
(_id, name)
VALUES(3, "‚»‚Ì‘¼")
/
insert into SubCategories
(_id, name, parent)
values(0,"‹Zp‘", "‘Ğ")
/
insert into SubCategories
(_id, name, parent)
values(1,"–Ÿ‰æ", "‘Ğ")
/
insert into SubCategories
(_id, name, parent)
values(2,"¬à", "‘Ğ")
/
insert into SubCategories
(_id, name, parent)
values(3,"ƒ‰ƒCƒgƒmƒxƒ‹","‘Ğ")


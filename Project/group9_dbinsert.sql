PROMPT project_lab9;


PROMPT DROP TABLES;

DROP TABLE book CASCADE CONSTRAINT;
DROP TABLE student CASCADE CONSTRAINT;
DROP TABLE orderlist CASCADE CONSTRAINT;
DROP TABLE orderdetail CASCADE CONSTRAINT;


PROMPT create tables;

CREATE TABLE book (
bn integer, 
title CHAR(50), 
author CHAR (50), 
price REAL, 
amount INTEGER, 
PRIMARY KEY (bn));

CREATE TABLE student(
sid integer, 
name CHAR(20),
gender CHAR(1),  
major CHAR (10), 
dislevel INTEGER, 
PRIMARY KEY (sid));

CREATE TABLE orderlist(
oid INTEGER, 
cardpay CHAR(20) , 
card INTEGER, 
sid INTEGER, 
orderdate DATE, 
tprice REAL, 
PRIMARY KEY (oid), 
FOREIGN KEY (sid) REFERENCES student (sid));

CREATE TABLE orderdetail(
bn integer, 
oid integer, 
sentTime DATE, 
PRIMARY KEY (oid,bn), 
FOREIGN KEY (oid) REFERENCES orderlist(oid), 
FOREIGN KEY (bn) REFERENCES book(bn));


PROMPT create triggers;

CREATE OR REPLACE TRIGGER cardpayerror 
Before INSERT OR UPDATE ON orderlist 
for each row 
BEGIN 
IF (:new.cardpay = 'credit card' AND(:new.card='' OR :new.card is NULL)) THEN 
RAISE_APPLICATION_ERROR(-20000, 'You should input your card number.');
END IF;
END;
/

CREATE OR REPLACE TRIGGER amountmin 
AFTER INSERT ON orderdetail 
FOR EACH ROW 
BEGIN 
UPDATE book SET amount = amount - 1 WHERE bn = :new.bn;
END;
/

CREATE OR REPLACE TRIGGER amountadd
AFTER DELETE ON orderdetail 
FOR EACH ROW 
BEGIN 
UPDATE book SET amount = amount + 1 WHERE bn = :old.bn;
END;
/

CREATE OR REPLACE TRIGGER calculate 
before insert ON orderdetail 
FOR EACH ROW 
DECLARE 
d integer; 
P real; 
BEGIN 
SELECT dislevel INTO d FROM student WHERE student.sid = (SELECT sid FROM orderlist WHERE orderlist.oid = :new.oid); 
select price into p from book where book.bn = :new.bn; 
UPDATE orderlist SET tprice = tprice + (1-(0.1*d))*p WHERE oid = :new.oid; 
END; 
/

CREATE OR REPLACE TRIGGER changeinsert 
AFTER insert or update ON orderdetail 
FOR EACH ROW 
DECLARE 
a real; 
s integer; 
BEGIN 
SELECT sid into s FROM orderlist WHERE :new.oid = orderlist.oid; 
SELECT SUM(tprice) INTO a FROM orderlist 
WHERE orderdate >= trunc(sysdate,'YYYY') 
GROUP BY (sid) HAVING orderlist.sid = (select sid from orderlist where oid= :new.oid); 
IF (a <= 2000 and a > 1000) THEN 
UPDATE student SET dislevel = 1 where sid = s; 
ELSIF a > 2000 THEN 
UPDATE student SET dislevel = 2 where sid = s; 
ELSE 
UPDATE student SET dislevel = 0 where sid = s; 
END IF; 
END; 
/

CREATE OR REPLACE TRIGGER calculatedel 
before delete ON orderdetail 
FOR EACH ROW 
DECLARE 
BEGIN 
UPDATE orderlist SET tprice = 0 WHERE oid = :old.oid; 
END; 
/

CREATE OR REPLACE TRIGGER changedel 
After delete ON orderdetail 
FOR EACH ROW 
DECLARE 
a real; 
s integer; 
BEGIN 
SELECT sid into s FROM orderlist WHERE :old.oid = orderlist.oid; 
SELECT SUM(tprice) INTO a FROM orderlist WHERE orderdate >= trunc(sysdate,'YYYY') GROUP BY (sid) HAVING orderlist.sid = (select sid from orderlist where oid= :old.oid); 
IF (a <= 2000 and a > 1000) THEN 
UPDATE student SET dislevel = 1 where sid = s; 
ELSIF a > 2000 THEN 
UPDATE student SET dislevel = 2 where sid = s; 
ELSE 
UPDATE student SET dislevel = 0 where sid = s; 
END IF; 
END; 
/

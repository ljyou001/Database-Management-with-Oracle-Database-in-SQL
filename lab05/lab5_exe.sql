SQL> CREATE TABLE STUDENTS(
  2  NAME VARCHAR(15) NOT NULL,
  3  STU_ID CHAR(3) NOT NULL,
  4  BDATE DATE NOT NULL,
  5  AGE INT,
  6  PRIMARY KEY (STU_ID));

Table created.

SQL> CREATE TABLE GRADES(
  2  STU_ID CHAR(3) NOT NULL,
  3  COURSE_CODE VARCHAR(8) NOT NULL,
  4  GRADE CHAR(1),
  5  PRIMARY KEY (STU_ID, COURSE_CODE),
  6  FOREIGN KEY (STU_ID) REFERENCES STUDENTS (STU_ID));

Table created.

SQL> CREATE TABLE GRADE_STAT(
  2  COURSE_CODE VARCHAR(8) NOT NULL,
  3  GRADE CHAR(1) NOT NULL,
  4  COUNT INT,
  5  PRIMARY KEY (COURSE_CODE, GRADE));

Table created.

SQL> CREATE OR REPLACE TRIGGER questionA
  2  BEFORE INSERT OR UPDATE ON grades
  3  FOR EACH ROW
  4  BEGIN
  5  IF (:new.grade <> 'A' AND :new.grade <> 'B' AND :new.grade <> 'C' AND :new.grade <> 'D' AND :new.grade <> 'E' AND :new.grade <> 'F') THEN
  6  RAISE_APPLICATION_ERROR(-20001, 'INVALID GRADE');
  7  END IF;
  8  END;
  9  /

Trigger created.

SQL> INSERT INTO GRADES VALUES('123', 'COMP1150', 'G');
INSERT INTO GRADES VALUES('123', 'COMP1150', 'G')
            *
ERROR at line 1:
ORA-20001: INVALID GRADE 
ORA-06512: at "E5251632.QUESTIONA", line 3 
ORA-04088: error during execution of trigger 'E5251632.QUESTIONA' 


SQL> CREATE OR REPLACE TRIGGER questionB1
  2  AFTER INSERT ON grades
  3  FOR EACH ROW
  4  DECLARE
  5   C INTEGER;
  6  BEGIN
  7   SELECT COUNT(*) INTO C FROM GRADE_STAT
  8   WHERE Course_code = :new.Course_code AND Grade = :new.Grade;
  9   IF (C = 0)THEN
 10    INSERT INTO GRADE_STAT VALUES(:new.Course_code, :new.Grade, 1);
 11   ELSE
 12    UPDATE GRADE_STAT SET count=count+1
 13    WHERE Course_code = :new.Course_code AND Grade = :new.Grade;
 14   END IF;
 15  END;
 16  /

Trigger created.

SQL> INSERT INTO STUDENTS VALUES('B','003','22-MAY-84',40);

1 row created.

SQL> INSERT INTO GRADES VALUES ('003', 'COMP1160', 'A');

1 row created.

SQL> INSERT INTO GRADES VALUES ('003', 'COMP1000', 'C');

1 row created.

SQL> SELECT * FROM GRADE_STAT
  2  ;

COURSE_C G      COUNT                                                           
-------- - ----------                                                           
COMP1160 A          1                                                           
COMP1000 C          1                                                           

SQL> DELETE FROM GRADES WHERE STU_ID = '002' OR STU_ID = '003';

2 rows deleted.

SQL> SELECT * FROM GRADE_STAT;

no rows selected

SQL> INSERT INTO GRADES VALUES ('003', 'COMP1160', 'A');

1 row created.

SQL> INSERT INTO STUDENTS VALUES('B','002','22-MAY-84',40);

1 row created.

SQL> INSERT INTO GRADES VALUES ('002', 'COMP1160', 'A');

1 row created.

SQL> select * from GRADE_STAT
  2  ;

COURSE_C G      COUNT                                                           
-------- - ----------                                                           
COMP1160 A          2                                                           

SQL> spool off

SQL> CREATE TABLE DEPARTMENTS(
  2  DNAME VARCHAR(15) NOT NULL,
  3  DNO INT,
  4  MANAGER CHAR(9) NOT NULL,
  5  PRIMARY KEY (DNO),
  6  UNIQUE (MANAGER));

SQL> ALTER TABLE DEPARTMENTS ADD CONSTRAINT DEPARTMENTS_CONST
  2  FOREIGN KEY (MANAGER) REFERENCES EMPLOYEES (HKID)
  3  INITIALLY DEFERRED DEFERRABLE;

Table altered.

SQL> CREATE TABLE EMPLOYEES(
  2  NAME VARCHAR(15) NOT NULL,
  3  HKID CHAR(9),
  4  BDATE DATE,
  5  SUPERVISOR CHAR(9),
  6  DEPT INT NOT NULL,
  7  PRIMARY KEY (HKID));

Table created.

SQL> INSERT INTO DEPARTMENTS VALUES ('Research',1,'333444555');

1 row created.

SQL> INSERT INTO DEPARTMENTS VALUES ('Administration',2,'987654321');

1 row created.

SQL> Select * FROM DEPARTMENTS;

DNAME                  DNO MANAGER                                              
--------------- ---------- ---------                                            
Research                 1 333444555                                            
Administration           2 987654321                                            

SQL> INSERT INTO EMPLOYEES VALUES ('John','123456789','22-MAY-1984','333444555',1);

1 row created.

SQL> INSERT INTO EMPLOYEES VALUES ('Alice','333444555','30-JUN-1987','987654321',1);

1 row created.

SQL> INSERT INTO EMPLOYEES VALUES ('Bob','987654321','14-DEC-1987','333444555',2);

1 row created.

SQL> Select * FROM EMPLOYEES;

NAME            HKID      BDATE     SUPERVISO       DEPT                        
--------------- --------- --------- --------- ----------                        
John            123456789 22-MAY-84 333444555          1                        
Alice           333444555 30-JUN-87 987654321          1                        
Bob             987654321 14-DEC-87 333444555          2                        

SQL> COMMIT;

Commit complete.

SQL> SELECT NAME, HKID, to_char(BDATE, 'DD/MM/YYYY') FROM EMPLOYEES;

NAME            HKID      TO_CHAR(BD                                            
--------------- --------- ----------                                            
John            123456789 22/05/1984                                            
Alice           333444555 30/06/1987                                            
Bob             987654321 14/12/1987                                            

SQL> UPDATE DEPARTMENTS SET DNAME='Admin' WHERE DNAME='Administration';

1 row updated.

SQL> SELECT * FROM DEPARTMENTS;

DNAME                  DNO MANAGER                                              
--------------- ---------- ---------                                            
Research                 1 333444555                                            
Admin                    2 987654321                                            

SQL> SELECT* FROM EMPLOYEES;

NAME            HKID      BDATE     SUPERVISO       DEPT                        
--------------- --------- --------- --------- ----------                        
John            123456789 22-MAY-84 333444555          1                        
Alice           333444555 30-JUN-87 987654321          1                        
Bob             987654321 14-DEC-87 333444555          2                        

SQL> SELECT NAME, HKID, to_char(BDATE, 'DD/MM/YYYY') FROM EMPLOYEES;

NAME            HKID      TO_CHAR(BD                                            
--------------- --------- ----------                                            
John            123456789 22/05/1984                                            
Alice           333444555 30/06/1987                                            
Bob             987654321 14/12/1987                                            

SQL> spool off

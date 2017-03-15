SQL> SELECT name, COUNT(pnumber)
  2  FROM EMPLOYEES, WORKS_ON
  3  WHERE EMPLOYEES.ssn = WORKS_ON.ssn
  4  GROUP BY EMPLOYEES.ssn, name;

NAME            COUNT(PNUMBER)                                                  
--------------- --------------                                                  
Ramesh                       1                                                  
Jennifer                     2                                                  
Ahmad                        2                                                  
Alicia                       2                                                  
Franklin                     4                                                  
Joyce                        2                                                  
John                         2                                                  
James                        1                                                  
John                         2                                                  

9 rows selected.

SQL> SELECT ssn, AVG(hours)
  2  FROM WORKS_ON
  3  GROUP BY ssn
  4  HAVING COUNT(pnumber) = 2;

SSN       AVG(HOURS)                                                            
--------- ----------                                                            
453453453         20                                                            
123456789         20                                                            
999887777         20                                                            
987654321       17.5                                                            
987987987         20                                                            
101010101         10                                                            

6 rows selected.

SQL> SELECT ssn
  2  FROM EMPLOYEES
  3  WHERE salary > (SELECT AVG(salary) FROM EMPLOYEES);

SSN                                                                             
---------                                                                       
333445555                                                                       
987654321                                                                       
666884444                                                                       
888665555                                                                       
888888888                                                                       
316316316                                                                       

6 rows selected.

SQL> SELECT ssn
  2  FROM EMPLOYEES
  3  WHERE salary <= ALL (SELECT MIN(salary) FROM EMPLOYEES);

SSN                                                                             
---------                                                                       
101010101                                                                       

SQL> SELECT ssn
  2  FROM EMPLOYEES E1
  3  WHERE NOT EXISTS (SELECT ssn FROM EMPLOYEES E2 WHERE E2.salary < E1.salary);

SSN                                                                             
---------                                                                       
101010101                                                                       

SQL> (SELECT ssn
  2  FROM EMPLOYEES)
  3  MINUS
  4  (SELECT ssn
  5  FROM WORKS_ON);

SSN                                                                             
---------                                                                       
142142142                                                                       
173173173                                                                       
316316316                                                                       
888888888                                                                       

SQL> SELECT DISTINCT name
  2  FROM EMPLOYEES
  3  WHERE NOT EXISTS
  4  (SELECT DISTINCT WORKS_ON.ssn FROM WORKS_ON WHERE EMPLOYEES.ssn = WORKS_ON.ssn);

NAME                                                                            
---------------                                                                 
Peter                                                                           
Jack                                                                            
Kevin                                                                           
Mary                                                                            

SQL> SELECT w1.ssn
  2  FROM WORKS_ON w1
  3  WHERE
  4  w1.pnumber = 3
  5  AND ssn NOT IN
  6  (SELECT ssn FROM PROJECTS, WORKS_ON w2
  7  WHERE w2.pnumber = PROJECTS.pnumber AND pname = 'ProductY');

SSN                                                                             
---------                                                                       
666884444                                                                       

SQL> spool off

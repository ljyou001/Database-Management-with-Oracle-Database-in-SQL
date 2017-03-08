SQL> SELECT SSN,HOURS
  2  FROM WORKS_ON
  3  WHERE PNUMBER = 30 AND HOURS>=15 AND HOURS<=30;

SSN            HOURS                                                            
--------- ----------                                                            
999887777         30                                                            
987654321         20                                                            

SQL> 
SQL> SELECT SSN, NAME, SALARY, SUPER_SSN, DNUMBER
  2  FROM EMPLOYEES E
  3  WHERE DNUMBER = 4
  4  ORDER BY SALARY DESC;

SSN       NAME                SALARY SUPER_SSN    DNUMBER                       
--------- --------------- ---------- --------- ----------                       
987654321 Jennifer             43000 888665555          4                       
316316316 Kevin                40000 987654321          4                       
987987987 Ahmad                25000 987654321          4                       
999887777 Alicia               25000 987654321          4                       

SQL> SELECT E1.NAME
  2  FROM EMPLOYEES E1, EMPLOYEES E2
  3  WHERE E1.SUPER_SSN = E2.SSN AND E2.SUPER_SSN IS NULL;

NAME                                                                            
---------------                                                                 
Franklin                                                                        
Jennifer                                                                        
Peter                                                                           

SQL> SELECT NAME
  2  FROM EMPLOYEES
  3  WHERE NAME LIKE '%a%';

NAME                                                                            
---------------                                                                 
Franklin                                                                        
Alicia                                                                          
Ramesh                                                                          
Ahmad                                                                           
James                                                                           
Mary                                                                            
Jack                                                                            

7 rows selected.

SQL> SELECT SSN
  2  FROM WORKS_ON W, PROJECTS P
  3  WHERE W.PNUMBER = P.PNUMBER AND P.PNAME = 'Reorganization'
  4  MINUS
  5  SELECT SSN
  6  FROM WORKS_ON W, PROJECTS P
  7  WHERE W.PNUMBER = P.PNUMBER AND (P.PNAME = 'ProductZ' OR P.PNAME = 'Newbenefits');

SSN                                                                             
---------                                                                       
888665555                                                                       

SQL> SELECT DISTINCT NAME
  2  FROM EMPLOYEES E, WORKS_ON W1, WORKS_ON W2
  3  WHERE E.SSN = W1.SSN AND E.SSN = W2.SSN AND W1.PNUMBER <> W2.PNUMBER;

NAME                                                                            
---------------                                                                 
Joyce                                                                           
John                                                                            
Ahmad                                                                           
Franklin                                                                        
Jennifer                                                                        
Alicia                                                                          

6 rows selected.

SQL> SELECT DISTINCT E1.NAME, E1.SALARY
  2  FROM EMPLOYEES E1, EMPLOYEES E2
  3  WHERE E1.SALARY < E2.SALARY;

NAME                SALARY                                                      
--------------- ----------                                                      
Alicia               25000                                                      
Peter                42000                                                      
Jack                 28000                                                      
John                 30000                                                      
Kevin                40000                                                      
Jennifer             43000                                                      
Joyce                25000                                                      
Ahmad                25000                                                      
Ramesh               38000                                                      
Franklin             40000                                                      
Mary                 23000                                                      

11 rows selected.

SQL> SELECT NAME,SALARY
  2  FROM EMPLOYEES
  3  MINUS
  4  SELECT DISTINCT E1.NAME, E1.SALARY
  5  FROM EMPLOYEES E1, EMPLOYEES E2
  6  WHERE E1.SALARY < E2.SALARY;

NAME                SALARY                                                      
--------------- ----------                                                      
James                55000                                                      

SQL> spool off

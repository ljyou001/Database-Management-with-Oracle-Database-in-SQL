PROMPT DROP TABLES;

DROP TABLE FLIGHTS CASCADE CONSTRAINT;

CREATE TABLE FLIGHTS
(FLIGHT_NO	VARCHAR(5)	NOT NULL,
 DEPART_TIME DATE,
 ARRIVE_TIME DATE,
 FARE	INT,
 SOURCE	VARCHAR(20),
 DEST	VARCHAR(20),
 PRIMARY KEY(FLIGHT_NO));

COMMIT;

PROMPT INSERT FLIGHTS TABLE;


INSERT INTO FLIGHTS VALUES('CX100', to_date('15/3/2015,12:00:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( '15/3/2015,16:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 2000, 'HK',	'Tokyo');
INSERT INTO FLIGHTS VALUES('CX101', to_date('15/3/2015,18:30:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( '15/3/2015,23:30:00', 'dd/mm/yyyy,hh24:mi:ss'), 4000, 'Tokyo',	'New York');
INSERT INTO FLIGHTS VALUES('CX102', to_date('15/3/2015,10:00:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( '15/3/2015,13:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 2000, 'HK',	'Beijing');
INSERT INTO FLIGHTS VALUES('CX103', to_date('15/3/2015,15:00:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( '15/3/2015,18:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 1500, 'Beijing',	'Tokyo');
INSERT INTO FLIGHTS VALUES('CX104', to_date('15/3/2015,10:00:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( '15/3/2015,14:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 1500, 'New York',	'Beijing');
INSERT INTO FLIGHTS VALUES('CX105', to_date('15/3/2015,4:00:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( 	'15/3/2015,09:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 1000, 'HK',	'New York');
INSERT INTO FLIGHTS VALUES('CX106', to_date('15/3/2015,23:40:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( '16/3/2015,03:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 5000, 'New York',	'LA');
INSERT INTO FLIGHTS VALUES('CX107', to_date('15/3/2015,8:00:00', 'dd/mm/yyyy,hh24:mi:ss'), to_date( 	'15/3/2015,11:00:00', 'dd/mm/yyyy,hh24:mi:ss'), 1500, 'Beijing',	'Tokyo');

COMMIT;


--JDBC version 2.1
--Database version 3.0

--Tables drop statements: 
DROP TABLE IF EXISTS projects;
--
DROP TABLE IF EXISTS department;
--
DROP TABLE IF EXISTS courses;
--
DROP TABLE IF EXISTS staff;
--
DROP TABLE IF EXISTS work_on;
--
DROP TABLE IF EXISTS give_course;
--

--Views drop statements: 
DROP TABLE IF EXISTS view_blablaview;
--

--Tables create and inserts: 
CREATE TABLE 'projects' ('p_id' VARCHAR(10),'p_title' VARCHAR(30),'funder' VARCHAR(10),'funding' INT, PRIMARY KEY('p_id'));
--
INSERT INTO projects (p_id, p_title, funder, funding) VALUES ('COMIC','COMIC','ESPRIT',100000);
--
INSERT INTO projects (p_id, p_title, funder, funding) VALUES ('OSCAR','OSCAR','SERC',23400);
--
INSERT INTO projects (p_id, p_title, funder, funding) VALUES ('GUIDE','Guide','SERC',34100);
--
INSERT INTO projects (p_id, p_title, funder, funding) VALUES ('MCSCW','Multimedia and CSCW','SERC',19782);
--
INSERT INTO projects (p_id, p_title, funder, funding) VALUES ('AN','Advanced Nextology','NERC',51200);
--
CREATE TABLE 'department' ('d_id' VARCHAR(5),'d_title' VARCHAR(10),'location' VARCHAR(15), PRIMARY KEY('d_id'));
--
INSERT INTO department (d_id, d_title, location) VALUES ('COMP','Computing','SECAMS Building');
--
INSERT INTO department (d_id, d_title, location) VALUES ('NEXT','Nextology','Nexto Building');
--
CREATE TABLE 'courses' ('c_id' VARCHAR(3),'c_title' VARCHAR(30),'code' VARCHAR(4),'year' VARCHAR(4),'d_id' VARCHAR(5), PRIMARY KEY('c_id'), FOREIGN KEY ('d_id') REFERENCES 'department'('d_id'));
--
INSERT INTO courses (c_id, c_title, code, year, d_id) VALUES ('MM','Multimedia Systems','361','3rd','COMP');
--
INSERT INTO courses (c_id, c_title, code, year, d_id) VALUES ('IOS','Introduction to Operating Systems','112c','1st','COMP');
--
INSERT INTO courses (c_id, c_title, code, year, d_id) VALUES ('DB','Databases','242','2nd','COMP');
--
INSERT INTO courses (c_id, c_title, code, year, d_id) VALUES ('PA','Programming in Assembler','111a','1st','COMP');
--
INSERT INTO courses (c_id, c_title, code, year, d_id) VALUES ('BN','Basic Nextology','110','1st','NEXT');
--
CREATE TABLE 'staff' ('s_id' VARCHAR(4),'initials' VARCHAR (4),'s_name' VARCHAR(15),'pos' VARCHAR(15),'qual' VARCHAR(5),'d_id' VARCHAR(5), PRIMARY KEY('s_id'), FOREIGN KEY ('d_id') REFERENCES 'department'('d_id'));
--
INSERT INTO staff (s_id, initials, s_name, pos, qual, d_id) VALUES ('JF','J. ','Finney','Lecturer','PhD','COMP');
--
INSERT INTO staff (s_id, initials, s_name, pos, qual, d_id) VALUES ('JAM','J.A. ','Mariani','Senior Lecturer','PhD','COMP');
--
INSERT INTO staff (s_id, initials, s_name, pos, qual, d_id) VALUES ('GSB','G.S. ','Blair','Senior Lecturer','PhD','COMP');
--
INSERT INTO staff (s_id, initials, s_name, pos, qual, d_id) VALUES ('ND','N. ','Davies','Professor','PhD','COMP');
--
INSERT INTO staff (s_id, initials, s_name, pos, qual, d_id) VALUES ('BB','B. ','Bear','Professor','BA','NEXT');
--
CREATE TABLE 'work_on' ('s_id' VARCHAR(4),'p_id' VARCHAR(10),'start_date' INT,'stop_date' INT, PRIMARY KEY('s_id', 'p_id'), FOREIGN KEY ('s_id') REFERENCES 'staff'('s_id'), FOREIGN KEY ('p_id') REFERENCES 'projects'('p_id'));
--
INSERT INTO work_on (s_id, p_id, start_date, stop_date) VALUES ('JAM','COMIC',1994,1998);
--
INSERT INTO work_on (s_id, p_id, start_date, stop_date) VALUES ('JAM','OSCAR',1989,1991);
--
INSERT INTO work_on (s_id, p_id, start_date, stop_date) VALUES ('ND','GUIDE',1997,1999);
--
INSERT INTO work_on (s_id, p_id, start_date, stop_date) VALUES ('JF','GUIDE',1998,1999);
--
INSERT INTO work_on (s_id, p_id, start_date, stop_date) VALUES ('GSB','MCSCW',1990,1994);
--
INSERT INTO work_on (s_id, p_id, start_date, stop_date) VALUES ('BB','AN',1985,1989);
--
CREATE TABLE 'give_course' ('s_id' VARCHAR(4),'c_id' VARCHAR(3), PRIMARY KEY('s_id', 'c_id'), FOREIGN KEY ('s_id') REFERENCES 'staff'('s_id'), FOREIGN KEY ('c_id') REFERENCES 'courses'('c_id'));
--
INSERT INTO give_course (s_id, c_id) VALUES ('JF','MM');
--
INSERT INTO give_course (s_id, c_id) VALUES ('ND','MM');
--
INSERT INTO give_course (s_id, c_id) VALUES ('GSB','IOS');
--
INSERT INTO give_course (s_id, c_id) VALUES ('JAM','DB');
--
INSERT INTO give_course (s_id, c_id) VALUES ('JAM','PA');
--
INSERT INTO give_course (s_id, c_id) VALUES ('BB','BN');
--

--Views create and inserts: 
CREATE TABLE view_blablaview ('c_id' VARCHAR(3),'c_title' VARCHAR(30));
--
INSERT INTO view_blablaview (c_id, c_title) VALUES ('MM','Multimedia Systems');
--
INSERT INTO view_blablaview (c_id, c_title) VALUES ('IOS','Introduction to Operating Systems');
--
INSERT INTO view_blablaview (c_id, c_title) VALUES ('DB','Databases');
--
INSERT INTO view_blablaview (c_id, c_title) VALUES ('PA','Programming in Assembler');
--

--Indexes of DB: 


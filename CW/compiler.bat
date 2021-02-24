@ECHO OFF
javac -cp "C:\Users\Daniel\Desktop\SCC201\210CW\CW\sqlite-jdbc-3.7.2.jar;.;target" -d target *.java

java -cp "C:\Users\Daniel\Desktop\SCC201\210CW\CW\sqlite-jdbc-3.7.2.jar;.;target" Coursework LSH.db

sqlite3 databasecopy1.db<testFile1.sql
PAUSE
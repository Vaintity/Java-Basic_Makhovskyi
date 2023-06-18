# Java-Basic_Makhovskyi

1. Create MySQL connection
2. In MySQL Workbench open and execute 'func_stor_script.sql' SQL Script
3. Compile javac test_1.java
4. (Ubuntu) Run java -classpath ./mysql-connector-j-8.0.33.jar:./ test_1 
5. For credentials use:
url = 'jdbc:mysql://localhost/func_storage' where 'func_storage' is your db name  
username = 'root' where 'root' is your username  
password = 'tmppasswd' where 'tmppasswd' is your password  

As for now this java tool can:
1) Check math function and add it to database
2) Check roots and add them to db
3) find function in db using provided roots  
Database includes:
1) 'func' table with 2 columns: 'idfunc' and 'funccontent'
2) 'roots' table with 2 columns: 'idroots' and 'rootscontent'
3) (unused)* 'pairs' table with 3 columns: 'idpairs', 'idroots' and 'idfunc'

* - supposed to have functionality to contain pairs of functions and roots

echo off
rem the jsp file need to be copied to the templates directory 
cd \jadAllFolders\jaddev\devSTS4.20\databaseutils\target
java -cp "\Users\jorge\Downloads\mysql-connector-j-9.1.0.jar;databaseutils-1.0.war" org.springframework.boot.loader.launch.WarLauncher

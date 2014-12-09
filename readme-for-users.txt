About JarManager

  Inspired by the utility tool found at http://www.hizum.net/diary/?date=20060919 (Japanese only)

1.Installation and Configuration.

 JAR files
	(1)all-in-one: jarmanager-allinone.jar, this conains all neccessary dependent jar files and you don't have to 
	worry about them.
	(2)minimum: jarmanager-minimum.jar, you have to update the startup shell script or batch file.
	
  You can copy or move the jarmanager-all.jar or jarmanager-minimum.jar to somewhere you want to install it.
  If you want to use all-in-one jar file, no need of any configurations.
  
  You can launch the app from that directory by simply double click it in Explorer/Finder or something, 
  or type "java -jar jarmanager-all.jar" within terminal or command prompt.

2. Directory usage.

  JarManager uses the following directory in order to save neccessary data or log files.
     
 (1)Windows XP
	C:\Documents and Settings\Username\Application Data\Username\JarManager
 (2)Windows 7
	C:\Users\Username\AppData\Roaming\Username\JarManager
 (3)Macintosh
    /Users/Username/Library/Application Support/JarManager
 (4)Other Windows (Vista, 8 or later)
    I don't have these machines, so I can't test them. Sorry about that.
 (5)Other unix
    T.B.D.
     
3. Trouble shooting.

 This app creates the lock file (.jarManager.lock) in the above directory upon startup.
 So, if the launch fails, 
 (1)You have already launched the app and it has become invisible.
 (2)If you sure that you are not running the app, then you would try to remove the lock file and try again. 
 
 If something went wrong, create JarManagerConfig.properties in above app directory with the following content and
 inspect the log file. The log file is generated in the app directory. (jarmanager.log and derby.log) 
	
	#application.loglevel=INFO #default 
	application.loglevel=DEBUG
	derby.logLevel=20000
	#derby.logSql=false #default
	derby.logSql=true
	
  derby.logLevel reffers to http://db.apache.org/derby/docs/10.4/tuning/rtunproper26985.html.
  But I think you don't have to worry about this logLevel at all.

4. Uninstall

  Remove the jar file ;) and also remove the app directory described above. No uninstaller is provided.

 
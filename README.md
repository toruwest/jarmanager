About JarManager
  You can see the contens of jar file, from file chooser or drag and drop it from explorer like software.
  And also you can regisit them, scan the specified directory from scan menu and regist it to 'catalog'.
  Once the jar files are registered, you can find the jar file which contains that class.

  Inspired by the utility tool found at http://www.hizum.net/diary/?date=20060919 (Japanese only)

  Yesterday, I found the similar software at http://sourceforge.net/projects/jarfinder/.

  I hope this tool is useful.

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


====  For developers ===================================================================================================
1.You need the following additional libraries.

(1)Aspect J 1.8.3
  Download aspectj-1.8.3.jar from https://eclipse.org/aspectj/downloads.php.
  This is an installer, so you have to execute and extract the jar files.
  Open the command line and execute "java -jar aspectj-1.8.3.jar".
  Follow the instructions of installer.

  You can find it in Maven central repository, but version is different.
  I'm not sure if we can use it instead of above file.

(2)You can download the following jar files with provided pom.xml.
 Before you run Maven, you may have to edit a config file. See section 2.

  	appframework-1.0.3.jar
  	swing-worker-1.1.jar
  	commons-lang-2.6.jar
  	aspects4swing-0.1.8.jar

(3)On Ubuntu, you need to install Derby separately by typing:

	sudo apt-get install sun-javadb-core

  This will install the required jar file at: /usr/share/javadb/lib/derby.jar

(4)If you use Eclipse, you'd better install ADJT plugin (You can still build the app without
  the plugin, but you will see several syntax error messages which caused by absense of it)

  See http://eclipse.org/ajdt/ for details. Update site is as follows.

  	Kepler : AJDT 2.2.3
    	http://download.eclipse.org/tools/ajdt/43/update
  	Luna   : AJDT 2.2.4
		http://download.eclipse.org/tools/ajdt/44/update

2. Build the app

  You can use Ant or Maven. But actually we run Ant's build script from within pom.xml.

  If you want to use Ant to build the app, you must execute Maven once in order to
  download required jar files.

  If you want to use Maven, there is no such restrictions.

(1)Before you build the app, edit build-package.properties to adapt to your environment.

(2)Now we can build the app.

  To build the app from terminal(Unix/Mac) or command prompt (Windows),

  Maven:
    mvn antrun:ant
  Ant:
    ant -f build-package.xml

  To build this app from within Eclipse, from Project explorer or Package explorer, right-click build-package.xml(Ant)
  or pom.xml(Maven), Run as -> Ant build or Maven build.

  Why don't I use the default file name 'build.xml' for Ant build file?  Because when I export Ant build file from Eclipse Export,
  it generates default filename and Eclipse will overwrite existing build.xml. I don't want it.

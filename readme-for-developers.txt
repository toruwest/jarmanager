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
   Copyright (C) 2017-2018 UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056
 
   This Program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.
 
   This Program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
   GNU General Public License for more details.
 
   You should have received a copy of the GNU General Public License
   along with UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056  see the file COPYING.  If not, see
   <http://www.gnu.org/licenses/>.

# NOVAMAG-JSON-Loader

This application is a data loader application that inserts new items in the NOVAMAG database. These new items are in JSON format.
The Database Management System used is PostgreSQL 9.

This application is a utility that can read JSON files containing information about one or several materials and load it into the database. The JSON file can reference some attached files, which are supposed that are located in the same path that such JSON file. The application can also cope with ZIP files containing a sub-directory tree. This sub-directory tree can contain several JSON files and their attached files. Again, the attached files of a JSON file must be located in the same path than the JSON file.
The loader application is intended to be invoked by the database administrator from the operative system command line specifying the path of the JSON or ZIP file as a command line argument.

Once the data is in JSON format, it can be loaded into the database by the NOVAMAG Loader Application. A JSON file can contain information of a solely material (i.e., a single JSON object) or of several materials (i.e., a JSON array). The attached files of these materials must be allocated in the same path than the JSON file for the loading.
The figure below shows an example invocation from Windows command line. The eventual attached files in the example are supposed to be in F:\data along with the JSON file:

java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader F:\data\Fe_Ni.json

The NOVAMAG Java Loader needs several jar libraries (note lib/* in the example above). These libraries provide methods to manage JSON format, JDBC database connectivity, JNDI services and logging.

If massive data loading is needed, the Java Loader Application can cope with ZIP compressed files. The ZIP files can contains a sub-directory tree. In each sub-directory new sub-directories or JSON files can be allocated. Again, each JSON file can contain information of a solely material or of several materials, and the attached files of these materials must be in the ZIP file allocated in the same path than the JSON file that references them.
The invocation is the same than in the JSON file case, except that the argument now is a ZIP file as illustrates the figure below.

java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader F:\data\Fe_Ta.zip

INSTALLATION
============

1) Copy into the installation folder these files:
- NOVAMAG-Java-Loader.jat
- log4j2.xml (change this log4 configuration file according your preferences)

2) Create the following subfolders
- backups
This folder is used to allocate database backups. Every time you use the loader a backup is made automatically before and after each load.
- lib
This folder must contain the following libraries:

	commons-io-2.5.jar
	fscontext-4.6-b01.jar
	json-20170516.jar
	log4j-api-2.8.1.jar
	log4j-core-2.8.jar
	log4j-slf4j-impl-2.8.jar
	postgresql-42.1.4.jar
	slf4j-api-1.7.24.jar

3) res
The res (resources) folder must contain:

- The log file (log4.log)

- The OS scripts that performs the backups (i.e., doBackUp.bat for Windows or doBackUp.sh for Unix)
  Copy the appropriate one into the res folder 

- The config.json file
	This file contains the paths to some of the folders. Copy it into the res folder.
	Don't forget to update PG_HOME value to the path for PostgreSQL binaries
	Typically this value could be:
	-- For Windows
		"PG_HOME": "C:\\Program Files\\PostgreSQL\\9.6\\bin\\"
	-- For Linux
		"PG_HOME": "/opt/PostgreSQL/9.6/bin/"

4) Create a JNDI context in the res folder, using the com.sun.jndi.fscontext.RefFSContextFactory
	The name of the context must be "jdbc/novamag"
	It has to provide the properties to connect to the database (user, password, host and database)
	using a PGPoolingDataSource object.
	For example, using Java:
	
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.fscontext.RefFSContextFactory");
		properties.setProperty(Context.PROVIDER_URL, "file:./res");

		Context context = new InitialContext(properties);
		
		PGPoolingDataSource source = new PGPoolingDataSource();
		source.setDataSourceName("Novamag Loader Datasource");
		source.setServerName(HOST);
		source.setDatabaseName(DATABASE);
		source.setUser(USER);
		source.setPassword(PASSWORD);
		source.setMaxConnections(10);
		
		context.rebind("jdbc/novamag", source);
	
	






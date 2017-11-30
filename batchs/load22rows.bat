SET PG_PATH="C:\Program Files\PostgreSQL\9.6\bin"
SET PGUSER=postgres
SET PGPASSWORD=postgres

REM Clean public schema
%PG_PATH%\psql -f ../sql/cleanPublic_PSM.sql

REM Create tables
%PG_PATH%\psql -f ../sql/schema_loader_v08_02.sql

REM Create Stores procedures and triggers
%PG_PATH%\psql -f ../sql/stored_procs_v2.sql

REM Load JSON files
cd ..
java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader data_for_demo\examples_database_1.json
java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader data_for_demo\FeGe.zip
java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader data_for_demo\FeTa.zip

cd batchs
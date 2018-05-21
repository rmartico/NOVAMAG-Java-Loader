set PGPASSWORD=postgres

set PG_HOME=C:\Program Files\PostgreSQL\9.6\bin\


set PATH=%PATH%;%PG_HOME%
dropdb -U postgres postgres

createdb -U postgres postgres

psql -U postgres < backup_todo.dmp


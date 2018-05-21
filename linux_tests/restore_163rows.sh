export PGPASSWORD=postgres

dropdb -U postgres postgres
createdb -U postgres postgres
psql -U postgres < backup_todo.dmp


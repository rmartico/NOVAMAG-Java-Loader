%PG_HOME%pg_dump -v -U postgres -d novamag -n public -a -E utf8 -b --inserts --disable-triggers -f %1
#
# Script to load the data
#
# The only argumen is the path to the json ot zip file to load
# for example:
# ./launcher newDataFolder/Fe-N.zip
#
java -cp NOVAMAG-Java-Loader.jar:lib/* json_loader.Loader $1

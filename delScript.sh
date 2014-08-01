#!/bin/bash
#This script clears the log files and config files and fs files
#FIRST TIME : chmod u+x delScript.sh
#then : ./delScript.sh
 
echo "Script started"

DIRDST=$HOME"/AosProjectDemo/DemoLocal2Nodes/Project2"

#Clear the log
rm -r $DIRDST"/log/*"
#Clear the filesystem
rm -r $DIRDST"/fs/1/*"
rm -r $DIRDST"/fs/2/*"
#Clear the config_file
rm -r $DIRDST"config_file"

echo "Script completed"

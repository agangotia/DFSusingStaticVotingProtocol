#!/bin/bash
#This script clears the log files and config files and fs files
#FIRST TIME : chmod u+x delScript.sh
#then : ./delScript.sh
 
echo "Script started"

#Clear the log
rm -R log/*
#Clear the filesystem
rm -r fs/1/*
rm -r fs/2/*
#Clear the config_file
rm -r config_file

echo "Script completed"

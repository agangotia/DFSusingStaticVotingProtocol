#This script clears the old build and copies the new contents
#!/bin/bash 
echo "Script started"
#This is the location where i want to copy
DIRDST=$HOME"/AosProjectDemo/DemoLocal2Nodes/Project2"
#This is the location from where i need to copy
DIRSRC=$HOME"/Workspace_AOS/Project2.git"

#Clear the bin
rm -R bin/*
#Clear the source
rm -R src/*
#Clear the build.xml
rm -r build.xml
#Clear the log
rm -R log/*
#clear the test
rm -R test/*
#Clear the filesystem
rm -r fs/1/*
rm -r fs/2/*
rm -r fs/3/*
#Clear the config_file
#rm -r config_file
cp -r $DIRSRC"/src" $DIRDST
cp -r $DIRSRC"/build.xml" $DIRDST

echo "Script completed"

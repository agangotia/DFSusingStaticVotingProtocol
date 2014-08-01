#This script clears the old build and copies the new contents
#!/bin/bash 
echo "Script started"
#This is the location where i want to copy
DIRDST=$HOME"/AosProjectDemo/DemoLocal2Nodes/Project2"
#This is the location from where i need to copy
DIRSRC=$HOME"/Workspace_AOS/Project2.git"

#Clear the bin
rm -r $DIRDST"/bin"
#Clear the source
rm -r $DIRDST"/src"
#Clear the build.xml
rm -r $DIRDST"build.xml"
#Clear the log
rm -r $DIRDST"/log/*"
#Clear the filesystem
rm -r $DIRDST"/fs/1/*"
rm -r $DIRDST"/fs/2/*"
#Clear the config_file
rm -r $DIRDST"config_file"
cp -r $DIRSRC"/src" $DIRDST
cp -r $DIRSRC"/build.xml" $DIRDST
echo "Copying successfull for "$DIRDST"/"process

echo "Script completed"
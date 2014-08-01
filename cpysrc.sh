#This script clears the old build and copies the new contents
#!/bin/bash 
echo "Script started"
#This is the location where i want to copy
DIRDST=$HOME"/AosProjectDemo/DemoLocal2Nodes/Project2"
#This is the location from where i need to copy
DIRSRC=$HOME"/Workspace_AOS/Project2.git"
#number of processes to be setup

rm -r $DIRDST"/bin"
rm -r $DIRDST"/src"
rm -r $DIRDST"build.xml"
cp -r $DIRSRC"/src" $DIRDST
cp -r $DIRSRC"/build.xml" $DIRDST
echo "Copying successfull for "$DIRDST"/"process

echo "Script completed"

#!/bin/sh
#
# This program creates an Android image viewing application.
# See http://code.google.com/p/androidbigimage
#
# Command-line usage:
# ./generate_application my_milan_subway_map.png "Milan Subway"
#
# First argument: path to your image
# Second argument: name you want to give to your new application
#
########################################################################

# Uncomment to enable verbose mode.
#set -x

IMAGE_FILEPATH=$1
APPLICATION_NAME=$2

# Generate namespace based on application name.
# TODO remove non [a-z0-9_] characters
APPLICATION_ID=`echo ${APPLICATION_NAME} | sed '-e s/ /_/g' | tr [A-Z] [a-z]`

# Copy the template.
cp -r template ${APPLICATION_ID}

# Copy image.
# TODO change extension if needed.
cp ${IMAGE_FILEPATH} ${APPLICATION_ID}/res/drawable/image.jpg

cd ${APPLICATION_ID}

# Replace Java namespace in files.
TMPFILE=tmp.txt
for FILE in \
src/fr/free/nrw/androidbigimage/AndroidBigImage.java \
src/fr/free/nrw/androidbigimage/AndroidBigImageView.java \
src/fr/free/nrw/androidbigimage/Animation.java \
src/fr/free/nrw/androidbigimage/AnimationCallBack.java \
src/fr/free/nrw/androidbigimage/SizeCallBack.java \
res/layout/main.xml \
AndroidManifest.xml \
.project
do
    sed "s/androidbigimage/androidbigimage.${APPLICATION_ID}/g" ${FILE} > ${TMPFILE}
    mv ${TMPFILE} ${FILE}
done

# Replace application name in files.
TMPFILE=tmp.txt
for FILE in \
res/values/strings.xml
do
    sed "s/AndroidBigImage/${APPLICATION_NAME}/g" ${FILE} > ${TMPFILE}
    mv ${TMPFILE} ${FILE}
done

# Create package in the filesystem.
mkdir src/fr/free/nrw/androidbigimage/${APPLICATION_ID}

# Move Java files into the created package.
mv src/fr/free/nrw/androidbigimage/*.java src/fr/free/nrw/androidbigimage/${APPLICATION_ID}

# Explain the next steps.
echo ""
echo "Done."
echo "Now download and open Eclipse from the Android SDK, if you don't have it yet."
echo 'In Eclipse, click "File", "Import...", "General", "Existing projects into Workspace", "Next", "Browse".'
echo "Select the ${PWD} directory."
echo 'Click "OK" and "Finish".'
echo 'In the Package Explorer, find project ${APPLICATION_ID} and right-click on it.'
echo 'Click on "Android Tools", "Export Signed Application Package...".'
echo 'Follow the wizard to create or reuse keys and aliases.'
echo 'Upload the generated apk file on http://market.android.com/publish/Home'


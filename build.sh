#!/usr/bin/env bash

DIR="$(cd "`dirname "$0"`/../";pwd)"
NAME="star"
PROJECT_DIR=${DIR:="/tmp/$NAME"}

PROF=$2

cd $PROJECT_DIR/

echo "debug:dir = "$PROJECT_DIR
echo "debug: name="$NAME

#设置profile
profile="dev"
if [ $# -ge 1 ]; then
    profile="$1"
fi

echo "Starting building"
echo "step 1. rm target dirs"
for project in $NAME;
do
    OUTPUT="${PROJECT_DIR}/${project}/output/"
    echo "remove dir ${OUTPUT}"
    rm -rf ${OUTPUT}
    if [ "$?" != "0" ];then
        echo "error remove dir ${OUTPUT}"
        exit 1
    fi
done

echo "debug:dir1 = "$PROJECT_DIR
echo "debug: name1="$NAME

echo "step 2. deploy"
 echo "Starting package branch: ${profile}"
 mvn -f $NAME/pom.xml -DskipTests=true -P $profile -e clean package install
ret=$?
if [ $ret -ne 0 ];then
    echo "===== maven build failure ====="
    exit $ret
else
    echo -n "===== maven build successfully! ====="
fi

echo "step 3. check"
for project in $NAME;do
    OUTPUT_FILE="${PROJECT_DIR}/${project}/target/${project}.zip"
   if [ ! -f ${PID_FILE} ];then
       echo "no target file found,file ${OUTPUT_FILE}"
       echo "deploy failed"
       exit 1
   fi
done

echo "step 4. unzip"
PATH=`ls -l $NAME|grep '^d'|awk '{print $9}'`
for project in $PATH
do
    echo $project
    #if [ -d $project ]
    #then
	OUTPUT="${PROJECT_DIR}/$NAME/${project}/output/"
    OUTPUT_FILE="${PROJECT_DIR}/$NAME/${project}/target/${project}-2.0-assembly.zip"
    echo "unzip file ${OUTPUT_FILE} to dir ${OUTPUT}"
    /bin/mkdir -p $OUTPUT
    /usr/bin/unzip -o -d $OUTPUT $OUTPUT_FILE
    #fi
done

echo "deploy ok"

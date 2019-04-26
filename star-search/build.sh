#!/usr/bin/env bash

DIR="$(cd "`dirname "$0"`/../";pwd)"
PROJECT_DIR=${DIR:="/tmp/star"}

cd $PROJECT_DIR/


#设置profile
profile="prod"
if [ $# -ge 1 ]; then
    profile="$1"
fi

echo "Starting building"
echo "step 1. rm target dirs"
for project in star;do
    OUTPUT="${PROJECT_DIR}/${project}/output/"
    echo "remove dir ${OUTPUT}"
    rm -rf ${OUTPUT}
    if [ "$?" != "0" ];then
        echo "error remove dir ${OUTPUT}"
        exit 1
    fi
done

echo "step 2. deploy"
 echo "Starting package branch: ${profile}"
 mvn -f star/pom.xml -DskipTests=true -P$profile -e clean package install
ret=$?
if [ $ret -ne 0 ];then
    echo "===== maven build failure ====="
    exit $ret
else
    echo -n "===== maven build successfully! ====="
fi

echo "step 3. check"
for project in star;do
    OUTPUT_FILE="${PROJECT_DIR}/${project}/target/${project}.zip"
   if [ ! -f ${PID_FILE} ];then
       echo "no target file found,file ${OUTPUT_FILE}"
       echo "deploy failed"
       exit 1
   fi
done

echo "step 4. unzip"
for project in star;do
    OUTPUT="${PROJECT_DIR}/${project}/output/"
    OUTPUT_FILE="${PROJECT_DIR}/${project}/target/${project}-*.zip"
    echo "unzip file ${OUTPUT_FILE} to dir ${OUTPUT}\n"
    mkdir -p ${OUTPUT}
    unzip -d ${OUTPUT} ${OUTPUT_FILE}
    if [ "$?" != "0" ];then
        echo "error unzip file ${OUTPUT_FILE} to dir ${OUTPUT}"
        exit 1
    fi
done
echo "deploy ok"

#!/bin/sh

echo "LIB_DIR is set to: $LIB_DIR" 
echo "KEY_STORE is set to: $KEY_STORE" 

for i in `ls $LIB_DIR`
do
CLASSPATH=$CLASSPATH:$LIB_DIR/$i
export CLASSPATH
done

echo "Classpath= $CLASSPATH"

cd $LIB_DIR/build/log
rm *
#nohup java  -Djavax.net.ssl.keyStore=/home/sshetty/classes/vayoodoot/trunk/scripts/myKeystore -Djavax.net.ssl.keyStorePassword=sachin com.vayoodoot.server.MainServer 1522 2>> /home/sshetty/classes/vayoodoot/trunk/build/log/err.log 1>> /home/sshetty/classes/vayoodoot/trunk/build/log/out.log &
nohup java  -Djavax.net.ssl.keyStore=$KEY_STORE -Djavax.net.ssl.keyStorePassword=$KEY_STORE com.vayoodoot.server.MainServer 1522 2>> /tmp/err.log 1>> /tmp/out.log &

sleep 3


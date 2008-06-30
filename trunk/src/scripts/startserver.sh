#!/bin/sh

echo "LIB_DIR is set to: $LIB_DIR" 
echo "SERVER_JAR is set to: $SERVER_JAR" 
echo "KEY_STORE is set to: $KEY_STORE" 

for i in `ls $LIB_DIR`
do
CLASSPATH=$CLASSPATH:$LIB_DIR/$i
export CLASSPATH
done

CLASSPATH=$SERVER_JAR:$CLASSPATH
export CLASSPATH

echo "Classpath= $CLASSPATH"

#nohup java  -Djavax.net.ssl.keyStore=/home/sshetty/classes/vayoodoot/trunk/scripts/myKeystore -Djavax.net.ssl.keyStorePassword=sachin com.vayoodoot.server.MainServer 1522 2>> /home/sshetty/classes/vayoodoot/trunk/build/log/err.log 1>> /home/sshetty/classes/vayoodoot/trunk/build/log/out.log &
nohup java  -Djavax.net.ssl.keyStore=$KEY_STORE -Djavax.net.ssl.keyStorePassword=$KEY_STORE_PASS com.vayoodoot.server.MainServer 19000 & 



sleep 3


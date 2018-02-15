@echo off
title Vencillio Server
color B
echo Vencillio is starting up on VPS...
java -cp bin;lib/gson-2.2.2.jar;lib/gson-2.2.2-sources.jar;lib/json-lib-2.4-jdk15.jar;lib/mysql-connector.jar;lib/netty-3.6.1.Final.jar;lib/xpp3_min-1.1.4c.jar;lib/xstream-1.3.1.jar; com.vencillio.Server
pause
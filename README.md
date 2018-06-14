# 三国手游服务端

##运行参数

java -Xms1024m -XX:-OmitStackTraceInFastThrow -Xmx1024m -XX:NewRatio=4 -XX:ThreadPriorityPolicy=42 -verbose:gc -XX:PermSize=64m -XX:MaxPermSize=64m -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseFastAccessorMethods -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:MaxTenuringThreshold=10 -XX:+DisableExplicitGC -Djava.ext.dirs=D:\SGB_Server\server\lib -jar server.jar

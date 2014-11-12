#!/bin/sh

AKKA_HOME="$(cd "$(cd "$(dirname "$0")"; pwd -P)"/..; pwd)"
JAVA_OPTS="-Xms1024M -Xmx1024M -Xss1M -XX:+UseParallelGC"
AKKA_CLASSPATH="$AKKA_HOME/lib/*:$AKKA_HOME/config"
AKKA_KERNEL_CLASS="pw.anisimov.tinker.kernel.TinkerModuleKernel"

start() {
        echo "Starting Tinker Service: "
        java $JAVA_OPTS -cp "$AKKA_CLASSPATH" -Dakka.home="$AKKA_HOME" -Dakka.kernel.quiet=true akka.kernel.Main $AKKA_KERNEL_CLASS "$@" >> /dev/null &
        echo $! > $AKKA_HOME/akka.pid
        echo "done."
}
stop() {
        echo "Shutting down Tinker Service: "
        kill `cat $AKKA_HOME/akka.pid`
        rm -f $AKKA_HOME/akka.pid
        echo "done."
}

case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  restart)
        stop
        sleep 10
        start
        ;;
  *)
        echo "Usage: $0 {start|stop|restart}"
esac

exit 0

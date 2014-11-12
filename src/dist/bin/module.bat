@echo off

title Reference Module

set AKKA_HOME=%~dp0..
set JAVA_OPTS=-Xmx1024M -Xms1024M -Xss1M -XX:+UseParallelGC
set AKKA_CLASSPATH=%AKKA_HOME%\lib\*;%AKKA_HOME%\config

java %JAVA_OPTS% -cp "%AKKA_CLASSPATH%" -Dakka.home="%AKKA_HOME%" akka.kernel.Main pw.anisimov.tinker.kernel.TinkerModuleKernel

@echo off

set APPDIR="%CD%\.application"
cd "%APPDIR%"

set PROPERTY_FOLDER="%APPDIR%"\property-folder
set PROJECT_FILE="project.jar"

set CONF=-property-folder %PROPERTY_FOLDER% -plugin-checksum-test no -install-plugin-help no -install-payload-plugins yes

set LIBDIR32=%pwd%lib\windows\x86;custom-lib\windows\x86
set LIBDIR64=%pwd%lib\windows\x64;custom-lib\windows\x64

if defined ProgramW6432 (
  @echo detected 64-bit OS
  set LIBDIR=%LIBDIR64%
  set JAVAEXE=jre/x64/bin/java
  set MAXHEAP=4096

) else (
  @echo detected 32-bit OS
  set LIBDIR=%LIBDIR32%
  set JAVAEXE=jre/x86/bin/java
  set MAXHEAP=512
)

REM if no integrated jre can be found try using the system version
if not exist %JAVAEXE% set JAVAEXE=java


REM optimized for jre 8 (09.07.2015)
cmd /k %JAVAEXE% -Xms256m -Xmx%MAXHEAP%m -Djava.library.path="%LIBDIR%" -jar "%PROJECT_FILE%" %CONF% --console-app-args %*

exit
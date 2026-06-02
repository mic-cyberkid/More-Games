@echo off
setlocal
if not exist out mkdir out
dir /s /b com\*.java > sources.txt
javac --enable-preview --release 21 -d out @sources.txt
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)
echo Build successful.
endlocal
